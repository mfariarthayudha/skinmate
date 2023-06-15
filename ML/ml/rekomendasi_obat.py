import sys
import numpy as np
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing import image
import json
import calendar
import time
import pandas as pd

model_path = "ModelingV3.h5"

model = load_model(model_path)

picture_path = (
    "dataset/3test/AcneVulgaris/acne-closed-comedo-003.jpg"
)

img = image.load_img(picture_path, target_size=(150, 150))
x = image.img_to_array(img)
x /= 255
x = np.expand_dims(x, axis=0)
images = np.vstack([x])
classes = model.predict(images, batch_size=10)

print(classes[0])
value = max(classes[0])
print(value)
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
print(skin_value)

path_data_rekomendasi = "Data_Rekomendasi.csv"
data_rekomendasi = pd.read_csv(path_data_rekomendasi, delimiter=";")

# disease: 1 = Acne Vulgaris, 2 = Actinic Keratosis, 3 = Nail Fungus, 4 = Psoriasis, 5 = Seborrheic Keratoses

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
json_file = open(str(id_predict) + ".json", "w")
json_file.write(json_object)
json_file.close()
