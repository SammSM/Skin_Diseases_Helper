# Skin Diseases Helper  

**Skin Diseases Helper** is an Android application that predicts possible skin diseases from photos using a trained neural network.  
The project combines three main parts:  

- 📱 **Android mobile app** – user-friendly interface for uploading or capturing photos  
- ⚙️ **Backend (FastAPI)** – handles requests and runs the trained model  
- 🤖 **Neural network** – built and trained in Python (Jupyter Notebook) for skin disease prediction  

This project demonstrates how **machine learning models** can be integrated into **mobile applications** through a lightweight backend API.  

---

## Supported Diseases  

The model is trained to recognize the following skin conditions:  
- **Acne**  
- **Bullous Disease**  
- **Eczema**  
- **Nail Disease**  
- **Rosacea**  
- **Vascular Tumors**  
- **Vasculitis**  

---

## ⚙️ How It Works  

1. The user opens the Android app and uploads or captures a skin photo.  
2. The image is sent to the **FastAPI backend**.  
3. The backend loads the trained neural network (`diseases_helper.h5`) and processes the image.  
4. The model predicts the most likely disease from the supported classes.  
5. The result is sent back to the mobile app and displayed to the user.  

---

## Project Structure
```
Skin-Diseases-Helper/
├── android_project/
│   └── ... 
├── backend/
│   ├── diseases_helper.h5
│   └── main.py
├── neural_network/
│   └── diseases_helper.ipynb
└── requirements.txt
```
---

## User Interface

