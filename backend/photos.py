from fastapi import APIRouter, Depends, UploadFile, File, HTTPException, status
import shutil
import os

from dependencies import require_role
from models import User


router = APIRouter(prefix="/api/fotos", tags=["Fotos"])
os.makedirs("uploads", exist_ok=True)


@router.post("/", status_code=status.HTTP_201_CREATED)
def upload_photo(
    file: UploadFile = File(...),
    current_user: User = Depends(
        require_role("familia", "organizacion", "admin")
    ),
):
    if file.content_type not in (
        "image/jpeg",
        "image/png",
        "image/webp",
    ):
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_CONTENT,
            detail="El archivo debe ser una imagen",
        )

    file_location = f"uploads/{file.filename}"

    with open(file_location, "wb+") as file_object:
        shutil.copyfileobj(file.file, file_object)

    return {
        "mensaje": "Foto subida",
        "url": f"/{file_location}",
    }