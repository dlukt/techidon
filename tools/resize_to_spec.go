package main

import (
	"image"
	"image/color"
	"image/draw"
	_ "image/jpeg"
	"image/png"
	"log"
	"math"
	"os"
)

// Simple bilinear scaling
func scale(src image.Image, rect image.Rectangle) image.Image {
	dst := image.NewRGBA(rect)
	
	xRatio := float64(src.Bounds().Dx()) / float64(rect.Dx())
	yRatio := float64(src.Bounds().Dy()) / float64(rect.Dy())

	for y := 0; y < rect.Dy(); y++ {
		for x := 0; x < rect.Dx(); x++ {
			srcX := float64(x) * xRatio
			srcY := float64(y) * yRatio
			
			x1 := int(math.Floor(srcX))
			y1 := int(math.Floor(srcY))
			x2 := int(math.Min(float64(x1+1), float64(src.Bounds().Dx()-1)))
			y2 := int(math.Min(float64(y1+1), float64(src.Bounds().Dy()-1)))
			
			ratX := srcX - float64(x1)
			ratY := srcY - float64(y1)
			
			// Get four pixels
			c11 := src.At(src.Bounds().Min.X+x1, src.Bounds().Min.Y+y1)
			c12 := src.At(src.Bounds().Min.X+x1, src.Bounds().Min.Y+y2)
			c21 := src.At(src.Bounds().Min.X+x2, src.Bounds().Min.Y+y1)
			c22 := src.At(src.Bounds().Min.X+x2, src.Bounds().Min.Y+y2)
			
			// Interpolate
			r, g, b, a := bilinearInterpolate(c11, c12, c21, c22, ratX, ratY)
			dst.Set(x, y, color.RGBA{r, g, b, a})
		}
	}
	return dst
}

func bilinearInterpolate(c11, c12, c21, c22 color.Color, tx, ty float64) (uint8, uint8, uint8, uint8) {
	r11, g11, b11, a11 := c11.RGBA()
	r12, g12, b12, a12 := c12.RGBA()
	r21, g21, b21, a21 := c21.RGBA()
	r22, g22, b22, a22 := c22.RGBA()

	// Helper to interpolate one channel
	interp := func(v11, v12, v21, v22 uint32) uint8 {
		top := float64(v11)*(1-tx) + float64(v21)*tx
		bottom := float64(v12)*(1-tx) + float64(v22)*tx
		return uint8((top*(1-ty) + bottom*ty) / 257) 
	}

	return interp(r11, r12, r21, r22),
		interp(g11, g12, g21, g22),
		interp(b11, b12, b21, b22),
		interp(a11, a12, a21, a22)
}

func main() {
	imgPath := "metadata/en-US/images/featureGraphic.png"
	
	file, err := os.Open(imgPath)
	if err != nil {
		log.Fatalf("Failed to open image: %v", err)
	}
	defer file.Close()

	img, _, err := image.Decode(file)
	if err != nil {
		log.Fatalf("Failed to decode image: %v", err)
	}

	bounds := img.Bounds()
	width := bounds.Dx()
	height := bounds.Dy()
	log.Printf("Original dimensions: %dx%d", width, height)

	targetW := 1024
	targetH := 500

	// Strategy: Scale to fit width (1024), then crop height to 500.
	// OR Scale to fit height (500), then crop width?
	// Since 1024 is much wider than 640, scaling to width 1024 will make height 1024.
	// Then we crop the middle 500. This preserves the full width content but loses top/bottom.
	// This is standard "Aspect Fill" for a wider target.
	
	scaleFactor := float64(targetW) / float64(width)
	newW := int(float64(width) * scaleFactor)
	newH := int(float64(height) * scaleFactor)
	
	log.Printf("Scaling to %dx%d", newW, newH)
	
	scaledImg := scale(img, image.Rect(0, 0, newW, newH))
	
	// Crop Center
	dst := image.NewRGBA(image.Rect(0, 0, targetW, targetH))
	
	cropY := (newH - targetH) / 2
	srcRect := image.Rect(0, cropY, targetW, cropY+targetH)
	
	draw.Draw(dst, dst.Bounds(), scaledImg, srcRect.Min, draw.Src)

	// Save
	outFile, err := os.Create(imgPath)
	if err != nil {
		log.Fatalf("Failed to create output file: %v", err)
	}
	defer outFile.Close()

	if err := png.Encode(outFile, dst); err != nil {
		log.Fatalf("Failed to encode png: %v", err)
	}

	log.Println("Successfully resized to 1024x500")
}
