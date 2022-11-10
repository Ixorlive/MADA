from fastapi import FastAPI, UploadFile
import random

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.post("/recognize_meters_data")
async def recognize_meters_data(meter_photo: UploadFile):
    return {"data": "".join([str(random.randint(0, 9)) for _ in range(8)])}
