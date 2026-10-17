from fastapi import APIRouter, UploadFile, File
import shutil
import os

router = APIRouter(prefix="/api/fotos", tags=["Fotos"])
os.makedirs("uploads", exist_ok=True)

@router.post("/")
def upload_photo(file: UploadFile = File(...)):
    file_location = f"uploads/{file.filename}"
    with open(file_location, "wb+") as file_object:
        shutil.copyfileobj(file.file, file_object)
    return {"mensaje": "Foto subida", "url": f"/{file_location}"}