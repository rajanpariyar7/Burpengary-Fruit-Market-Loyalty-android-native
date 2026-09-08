from PIL import Image, ImageDraw

# Create a 1024x1024 image with black background
img = Image.new('RGB', (1024, 1024), color=(0, 0, 0))
draw = ImageDraw.Draw(img)

# Basket (bottom half)
# Points for a basket shape
basket_color = (205, 133, 63) # Peru brown
basket_outline = (139, 69, 19) # SaddleBrown
draw.polygon([(200, 500), (824, 500), (750, 850), (274, 850)], fill=basket_color, outline=basket_outline, width=10)

# Red shape (apple?)
draw.polygon([(400, 300), (600, 300), (500, 500)], fill=(220, 20, 60))
draw.ellipse([350, 350, 550, 550], fill=(220, 20, 60))

# Blue square
draw.rectangle([580, 280, 780, 480], fill=(0, 0, 139))

# Purple oval
draw.ellipse([200, 400, 400, 700], fill=(138, 43, 226))

# Green circles
draw.ellipse([550, 500, 700, 650], fill=(144, 238, 144))
draw.ellipse([650, 480, 780, 610], fill=(144, 238, 144))

img.save('app/src/main/res/drawable/ic_launcher_foreground.png')
print("Image generated successfully.")
