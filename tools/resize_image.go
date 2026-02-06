package main

import (
	"image"
	"image/draw"
	"image/png"
	"log"
	"os"
)

func main() {
	imgPath := "../metadata/en-US/images/featureGraphic.png"
	file, err := os.Open(imgPath)
	if err != nil {
		log.Fatalf("Failed to open image: %v", err)
	}
	defer file.Close()

	img, err := png.Decode(file)
	if err != nil {
		log.Fatalf("Failed to decode png: %v", err)
	}

	bounds := img.Bounds()
	width := bounds.Dx()
	height := bounds.Dy()

	log.Printf("Current dimensions: %dx%d", width, height)

	if width == 1024 && height == 500 {
		log.Println("Dimensions are already correct.")
		return
	}

	// Target dimensions
	targetW := 1024
	targetH := 500

	// Create new image
	dst := image.NewRGBA(image.Rect(0, 0, targetW, targetH))

	// Simple center crop strategy (assuming width is usually 1024 or larger)
	// If the image is square 1024x1024, we take the middle 500 lines.
	
	// Calculate source rect
	// We want to center the crop.
    // If source width is different, we might need to scale first, but standard library doesn't have high quality scaling easily. 
    // Let's assume the generated image is at least 1024 wide. If it's 1024x1024:
    
    var srcRect image.Rectangle
    
    // Scale isn't trivial in pure Go without libraries, but 'draw' can do nearest neighbor or we can just crop if the resolution is high enough.
    // Given 'generate_image' usually makes 1024x1024, we can just crop the vertical center.
    
    if width != targetW {
        log.Printf("Warning: Width is %d, expected %d. This script only handles height cropping for 1024 width images effectively. Proceeding with center crop anyway.", width, targetW)
    }

    yOffset := (height - targetH) / 2
    srcRect = image.Rect(0, yOffset, targetW, yOffset+targetH)

	draw.Draw(dst, dst.Bounds(), img, srcRect.Min, draw.Src)

	outFile, err := os.Create(imgPath)
	if err != nil {
		log.Fatalf("Failed to create output file: %v", err)
	}
	defer outFile.Close()

	if err := png.Encode(outFile, dst); err != nil {
		log.Fatalf("Failed to encode png: %v", err)
	}

	log.Println("Successfully cropped/resized to 1024x500")
}
