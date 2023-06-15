import os
import tensorflow as tf
import numpy as np
from tensorflow.keras.models import load_model
from tensorflow.keras.applications.inception_resnet_v2 import InceptionResNetV2, preprocess_input
from tensorflow.keras.preprocessing import image
import tensorflow_hub as hub
import json
import calendar
import time
import pandas as pd

model_path = "ModelingFinalV2.h5"

# Fungsi khusus untuk memuat model yang mengandung KerasLayer
def load_custom_model(path):
    return tf.keras.models.load_model(path, custom_objects={'KerasLayer': hub.KerasLayer}, compile=False)

model = load_custom_model(model_path)

picture_folder = "datasetv2/2validation/AcneVulgaris"  # Folder yang berisi gambar-gambar
output_folder = "AcneVulgaris"  # Folder tujuan untuk menyimpan gambar yang telah diproses

# Buat folder output jika belum ada
if not os.path.exists(output_folder):
    os.makedirs(output_folder)

# Mendapatkan daftar file di dalam folder
files = os.listdir(picture_folder)

total_predictions = 0
correct_predictions = 0
wrong_predictions = 0

for filename in files:
    if filename.endswith((".jpg", ".jpeg", ".png")):
        picture_path = os.path.join(picture_folder, filename)
        img = image.load_img(picture_path, target_size=(224, 224))  # Mengubah ukuran gambar menjadi 224x224
        x = image.img_to_array(img)
        x = np.expand_dims(x, axis=0)
        x = preprocess_input(x)
        classes = model.predict(x)

        value = max(classes[0])
        index_value = np.argmax(classes[0])
        skin_value = ""
        kind_model = "disease"  # Set jenis model yang sesuai
        if kind_model == "disease":
            if index_value == 0:
                skin_value = "Acne Vulgaris"
            elif index_value == 1:
                skin_value = "Actinic Keratosis"
            elif index_value == 2:
                skin_value = "Nail Fungus"
            elif index_value == 3:
                skin_value = "Psoriasis"
            elif index_value == 4:
                skin_value = "Seborrheic Keratoses"
        print(f"Image: {filename}, Result: {skin_value}")

        path_data_rekomendasi = "Data_Rekomendasi.csv"
        data_rekomendasi = pd.read_csv(path_data_rekomendasi, delimiter=";")

        # disease: 0 = Acne Vulgaris, 1 = Actinic Keratosis, 2 = Nail Fungus, 3 = Psoriasis, 4 = Seborrheic Keratoses

        if skin_value == "Acne Vulgaris":
            hasil_rekomendasi = data_rekomendasi.query("Rekomendasi == 1")
        elif skin_value == "Actinic Keratosis":
            hasil_rekomendasi = data_rekomendasi.query("Rekomendasi == 2")
        elif skin_value == "Nail Fungus":
            hasil_rekomendasi = data_rekomendasi.query("Rekomendasi == 3")
        elif skin_value == "Psoriasis":
            hasil_rekomendasi = data_rekomendasi.query("Rekomendasi == 4")
        elif skin_value == "Seborrheic Keratoses":
            hasil_rekomendasi = data_rekomendasi.query("Rekomendasi == 5")

        rekomendation_list = []
        for value in range(len(hasil_rekomendasi)):
            rekomendation_list.append(hasil_rekomendasi.iloc[value][1])

        id_predict = calendar.timegm(time.gmtime())
        dictionary = {
            "error": "false",
            "message": "success",
            "id": id_predict,
            "resultDetection": skin_value,
            "rekomendationList": rekomendation_list,
        }
        json_object = json.dumps(dictionary, indent=4)
        print(json_object)
        json_file = open(os.path.join(output_folder, str(id_predict) + ".json"), "w")
        json_file.write(json_object)
        json_file.close()

        # Menghitung total prediksi
        total_predictions += 1
        if skin_value == "Acne Vulgaris":
            correct_predictions += 1
        else:
            wrong_predictions += 1

print("Total Prediksi: ", total_predictions)
print("Prediksi Benar: ", correct_predictions)
print("Prediksi Salah: ", wrong_predictions)
