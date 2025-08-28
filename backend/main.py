from fastapi import FastAPI, File, UploadFile
import numpy as np
import cv2
import tensorflow as tf

app = FastAPI()

model = tf.keras.models.load_model("diseases_helper.h5")

CLASS_NAMES = ['Acne', 'Bullous Disease', 'Eczema', 'Nail Disease', 'Rosacea', 'Vascular Tumors', 'Vasculitis']

def preprocess_image(image_bytes):
    np_arr = np.frombuffer(image_bytes, np.uint8)
    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    img = cv2.resize(img, (128, 128))
    img = img.astype("float32") / 255.0
    img = np.expand_dims(img, axis=0)
    return img

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    image_bytes = await file.read()
    img = preprocess_image(image_bytes)

    preds = model.predict(img)
    pred_class = CLASS_NAMES[np.argmax(preds)]
    confidence = float(np.max(preds))

    if confidence >= 0.7:
        return pred_class
    else:
        return "No diseases detected!"
