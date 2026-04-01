import React, { useState } from "react";
import axios from "axios";
import { Container, Button, ProgressBar, Spinner, Card } from "react-bootstrap";

export default function ToolPage({ title, endpoint }) {
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [resultUrl, setResultUrl] = useState(null);

  const [startPage, setStartPage] = useState("");
  const [endPage, setEndPage] = useState("");

  const [angle, setAngle] = useState("90");
  const [pageNums, setPageNums] = useState("all");

  const [userPassword, setUserPassword] = useState("");
  const [ownerPassword, setOwnerPassword] = useState("");
  const [allowPrinting, setAllowPrinting] = useState(true);
  const [allowCopying, setAllowCopying] = useState(false);

  const isMerge = endpoint.includes("merge");
  const isSplit = endpoint.includes("split");
  const isRotate = endpoint.includes("rotate");
  const isUnlock = endpoint.includes("unlock");
  const isLock = endpoint.includes("lock") && !isUnlock;

  const handleFileChange = (e) => {
    setFiles(Array.from(e.target.files));
  };

  const handleSubmit = async () => {
    if (files.length === 0) {
      alert("Please upload file");
      return;
    }

    if (isMerge && files.length < 2) {
      alert("Please select at least 2 files for merging");
      return;
    }

    if (isUnlock && !userPassword) {
      alert("Password is mandatory");
      return;
    }

    if (isLock && !userPassword) {
      alert("User password is mandatory");
      return;
    }

    setLoading(true);
    setProgress(0);
    setResultUrl(null);

    try {
      const formData = new FormData();

      files.forEach((file) => {
        if (isMerge) {
          formData.append("files", file);
        } else {
          formData.append("file", file);
        }
      });

      if (isSplit) {
        formData.append("startPage", startPage || 1);
        formData.append("endPage", endPage || -1);
      }

      if (isRotate) {
        formData.append("angle", angle);
        formData.append("pageNums", pageNums);
      }

      if (isLock) {
        formData.append("userPassword", userPassword);
        formData.append("ownerPassword", ownerPassword);
        formData.append("allowPrinting", allowPrinting);
        formData.append("allowCopying", allowCopying);
      }

      if (isUnlock) {
        formData.append("userPassword", userPassword);
      }

      const response = await axios.post(
        `https://pdf-tools-hub-2.onrender.com${endpoint}`,
        formData,
        {
          responseType: "blob",
          onUploadProgress: (e) => {
            const percent = Math.round((e.loaded * 100) / e.total);
            setProgress(percent);
          },
        }
      );

      const blob = new Blob([response.data], {
        type: response.headers["content-type"],
      });

      const url = window.URL.createObjectURL(blob);
      setResultUrl(url);

    } catch (error) {
      console.error("Error:", error);

      if (error.response && error.response.data instanceof Blob) {
        const reader = new FileReader();

        reader.onload = () => {
          const message = reader.result.toLowerCase();

          if (message.includes("incorrect password")) {
            alert("Incorrect password");
          } else if (message.includes("password is required")) {
            alert("Password is mandatory");
          } else {
            alert(message || "Error processing file");
          }
        };

        reader.readAsText(error.response.data);
      } else {
        alert("Server not responding");
      }
    }

    setLoading(false);
  };

  const getAcceptedTypes = () => {
    if (endpoint.includes("pdf-to-word")) return ".pdf";
    if (endpoint.includes("word-to-pdf")) return ".doc,.docx";
    if (endpoint.includes("excel-to-pdf")) return ".xls,.xlsx";
    return "*";
  };

  const getFileName = (endpoint) => {
    if (endpoint.includes("pdf-to-word")) return "converted.docx";
    if (endpoint.includes("word-to-pdf")) return "converted.pdf";
    if (endpoint.includes("excel-to-pdf")) return "converted.pdf";
    return "converted-file";
  };


  const getAcceptedTypes = () => {
    if (endpoint.includes("pdf-to-word")) return ".pdf";
    if (endpoint.includes("word-to-pdf")) return ".doc,.docx";
    if (endpoint.includes("excel-to-pdf")) return ".xls,.xlsx";
    return "*";
  };

  return (
    <Container className="mt-5">
      <Card className="p-4 shadow-lg text-center">
        <h3>{title}</h3>

        <input
          type="file"
          className="form-control mt-3"
          multiple={isMerge}
          accept={getAcceptedTypes()}
          onChange={handleFileChange}
        />

        {files.length > 0 && (
          <div className="mt-3 text-start">
            <strong>Selected Files:</strong>
            <ul>
              {files.map((file, index) => (
                <li key={index}>{file.name}</li>
              ))}
            </ul>
          </div>
        )}

        <Button className="mt-3" onClick={handleSubmit} disabled={loading}>
          {loading ? "Processing..." : "Confirm"}
        </Button>

        {loading && (
          <>
            <Spinner className="mt-3" />
            <ProgressBar now={progress} label={`${progress}%`} className="mt-2" />
          </>
        )}

        {resultUrl && (
          <>
            <a href={resultUrl} download className="btn btn-success mt-3">
              Download File
            </a>

            <div className="mt-4">
              <iframe
                src={resultUrl}
                width="100%"
                height="500px"
                title="Preview"
                style={{ border: "1px solid #ccc" }}
              />
            </div>
          </>
        )}
      </Card>
    </Container>
  );
}
