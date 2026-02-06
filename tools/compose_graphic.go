package main

import (
	"image"
	"image/color"
	"image/draw"
	_ "image/jpeg" // Register JPEG decoder
	"image/png"
	"log"
	"math"
	"os"
)

// Simple bilinear scaling to avoid external dependencies
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
	// Paths
	bgPath := "metadata/en-US/images/featureGraphicBg.png"
	iconPath := "design/techidon_play_store_512.png" 
	outPath := "metadata/en-US/images/featureGraphic.png"

	log.Println("Processing...")
	log.Printf("Background: %s", bgPath)
	log.Printf("Icon: %s", iconPath)

	// 1. Load Background (could be JPEG)
	bgFile, err := os.Open(bgPath)
	if err != nil {
		log.Fatalf("Failed to open bg: %v", err)
	}
	defer bgFile.Close()
	bgImg, _, err := image.Decode(bgFile)
	if err != nil {
		log.Fatalf("Failed to decode bg: %v", err)
	}

	// 2. Load Icon
	iconFile, err := os.Open(iconPath)
	if err != nil {
		log.Fatalf("Failed to open icon: %v", err)
	}
	defer iconFile.Close()
	iconImg, _, err := image.Decode(iconFile)
	if err != nil {
		log.Fatalf("Failed to decode icon: %v", err)
	}

	// 3. Prepare target dimensions
	targetW := 1024
	targetH := 500

	// 4. Process Background: Scale to Cover
	bgBounds := bgImg.Bounds()
	bgW := bgBounds.Dx()
	bgH := bgBounds.Dy()
	
	scaleX := float64(targetW) / float64(bgW)
	scaleY := float64(targetH) / float64(bgH)
	scaleFactor := math.Max(scaleX, scaleY)
	
	newBgW := int(float64(bgW) * scaleFactor)
	newBgH := int(float64(bgH) * scaleFactor)
	
	log.Printf("Scaling background from %dx%d to %dx%d to cover target %dx%d", bgW, bgH, newBgW, newBgH, targetW, targetH)
	
	scaledBg := scale(bgImg, image.Rect(0, 0, newBgW, newBgH))
	
	// Crop center of scaled background
	finalImg := image.NewRGBA(image.Rect(0, 0, targetW, targetH))
	
	cropX := (newBgW - targetW) / 2
	cropY := (newBgH - targetH) / 2
	
	draw.Draw(finalImg, finalImg.Bounds(), scaledBg, image.Point{cropX, cropY}, draw.Src)

	// 5. Resize and Draw Icon
	// Desired icon height: 50% of 500 = 250px
	iconH := 250
	iconW := int(float64(iconImg.Bounds().Dx()) * (float64(iconH) / float64(iconImg.Bounds().Dy())))

	log.Printf("Scaling icon from %dx%d to %dx%d", iconImg.Bounds().Dx(), iconImg.Bounds().Dy(), iconW, iconH)
	
	resizedIcon := scale(iconImg, image.Rect(0, 0, iconW, iconH))

	// Center icon
	iconX := (targetW - iconW) / 2
	iconY := (targetH - iconH) / 2

	draw.Draw(finalImg, image.Rect(iconX, iconY, iconX+iconW, iconY+iconH), resizedIcon, image.Point{0, 0}, draw.Over)

	// 6. Save
	outFile, err := os.Create(outPath)
	if err != nil {
		log.Fatalf("Failed to create out file: %v", err)
	}
	defer outFile.Close()

	if err := png.Encode(outFile, finalImg); err != nil {
		log.Fatalf("Failed to save png: %v", err)
	}

	log.Println("Feature graphic composited successfully.")
}
