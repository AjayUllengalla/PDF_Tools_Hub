# PDF Tools HUB

A full-stack web application for PDF and document utility operations.

- **Frontend:** React + Vite UI
- **Backend:** Spring Boot REST API

## 📁 Repository Structure

- `backend/pdftoolshub` — Spring Boot backend application
- `frontend/pdfFrontend` — React frontend application

## 🚀 Features

- PDF compression
- PDF merge
- PDF split
- PDF rotate
- PDF lock / unlock
- PDF → Word conversion
- Word → PDF conversion
- Excel → PDF conversion

## 🧩 Backend Details

- Java 17
- Spring Boot `4.0.4`
- Apache PDFBox for PDF manipulation
- iText 7 for PDF security operations
- Apache POI for Word/Excel conversion
- OpenPDF for PDF creation

## 🧪 Frontend Details

- React 19
- Vite
- Bootstrap 5
- Axios for REST API calls
- React Router

## ▶️ Run Locally

### Backend

```powershell
cd backend\pdftoolshub
./mvnw spring-boot:run
```

If you are on Windows and `./mvnw` does not work, use:

```powershell
cd backend\pdftoolshub
mvnw.cmd spring-boot:run
```

The backend will start on `http://localhost:8080` by default.

### Frontend

```powershell
cd frontend\pdfFrontend
npm install
npm run dev
```

The frontend will typically start on `http://localhost:5173`.

## 🌐 API Endpoints

### Conversion
- `POST /pdf/convert/pdf-to-word`
- `POST /pdf/convert/word-to-pdf`
- `POST /pdf/convert/excel-to-pdf`

### Editing
- `POST /pdf/edit/merge`
- `POST /pdf/edit/split`
- `POST /pdf/edit/rotate`

### Security
- `POST /pdf/security/lock`
- `POST /pdf/security/unlock`

### Optimization
- `POST /pdf/optimize/compress`

## 🔌 Notes

- Backend accepts multipart file uploads.
- CORS is enabled for frontend integration.
- Maximum upload size is configured in `backend/pdftoolshub/src/main/resources/application.properties`.

## 📘 Further Customization

- Modify `backend/pdftoolshub/src/main/resources/application.properties` to change temporary directory or upload limits.
- Adjust the frontend UI in `frontend/pdfFrontend/src`.

## 💡 Helpful Commands

- Backend build: `cd backend\pdftoolshub && ./mvnw clean package`
- Frontend build: `cd frontend\pdfFrontend && npm run build`
