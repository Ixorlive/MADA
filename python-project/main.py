import aiofiles
from fastapi import FastAPI, File, Request
import os

from recognition_model import MetersPredictor

os.environ['CUDA_VISIBLE_DEVICES'] = '-1'

app = FastAPI()
app.state.predictor = MetersPredictor(
    "models/segmentation_model",
    "models/meters_prediction_model/model_final.pth"
)

api_key = "5zoTBZVQU50fCtyJFjhq2NIbu7clLn4o"
model_id = "d7d0d082-8725-42ce-825f-c6f273d6615d"
url = f"https://app.nanonets.com/api/v2/OCR/Model/{model_id}/LabelFile/"


@app.post("/recognize_meters_data")
async def recognize_meters_data(request: Request, file: bytes = File(...)):
    async with aiofiles.open("tmp/img_for_prediction.jpg", 'wb') as out_file:
        await out_file.write(file)  # async write
    try:
        prediction = request.app.state.predictor.predict("tmp/img_for_prediction.jpg")
    except:
        return None
    return prediction
