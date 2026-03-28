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

  // ✅ FIXED CONDITIONS
  const isMerge = endpoint.includes("merge");
  const isSplit = endpoint.includes("split");
  const isRotate = endpoint.includes("rotate");
  const isUnlock = endpoint.includes("unlock"); // ✅ first
  const isLock = endpoint.includes("lock") && !isUnlock; // ✅ prevent conflict

  const handleFileChange = (e) => {
    setFiles(Array.from(e.target.files));
  };

  const handleSubmit = async () => {
    if (files.length === 0) {
      alert("Please upload file");
      return;
    }

    // ✅ Merge validation
    if (isMerge && files.length < 2) {
      alert("Please select at least 2 files for merging");
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
        `http://localhost:8080${endpoint}`,
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
      console.error(error);
      alert("Error processing file");
    }

    setLoading(false);
  };

  return (
    <Container className="mt-5">
      <Card className="p-4 shadow-lg text-center">
        <h3>{title}</h3>

        {/* FILE INPUT */}
        <input
          type="file"
          className="form-control mt-3"
          multiple={isMerge}
          onChange={handleFileChange}
        />

        {/* ✅ SHOW FILE LIST */}
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

        {/* ✅ MERGE GUIDE */}
        {isMerge && (
          <p className="text-muted mt-2">
            Hold <b>Ctrl</b> (Windows) or <b>Cmd</b> (Mac) to select multiple files
          </p>
        )}

        {/* SPLIT */}
        {isSplit && (
          <>
            <input
              type="number"
              placeholder="Start Page"
              className="form-control mt-2"
              onChange={(e) => setStartPage(e.target.value)}
            />
            <input
              type="number"
              placeholder="End Page"
              className="form-control mt-2"
              onChange={(e) => setEndPage(e.target.value)}
            />
          </>
        )}

        {/* ROTATE */}
        {isRotate && (
          <>
            <select
              className="form-control mt-2"
              onChange={(e) => setAngle(e.target.value)}
            >
              <option value="90">Rotate 90°</option>
              <option value="180">Rotate 180°</option>
              <option value="270">Rotate 270°</option>
            </select>

            <input
              type="text"
              placeholder="Page numbers (e.g. 1,2 or all)"
              className="form-control mt-2"
              onChange={(e) => setPageNums(e.target.value)}
            />
          </>
        )}

        {/* LOCK */}
        {isLock && (
          <>
            <input
              type="password"
              placeholder="User Password"
              className="form-control mt-2"
              onChange={(e) => setUserPassword(e.target.value)}
            />

            <input
              type="password"
              placeholder="Owner Password (optional)"
              className="form-control mt-2"
              onChange={(e) => setOwnerPassword(e.target.value)}
            />
          </>
        )}

        {/* ✅ UNLOCK FIXED */}
        {isUnlock && (
          <>
            <input
              type="password"
              placeholder="Enter PDF password"
              className="form-control mt-2"
              onChange={(e) => setUserPassword(e.target.value)}
            />
            <small className="text-muted">
              Enter the password used to lock this PDF
            </small>
          </>
        )}

        {/* BUTTON */}
        <Button className="mt-3" onClick={handleSubmit} disabled={loading}>
          {loading ? "Processing..." : "Convert"}
        </Button>

        {/* LOADING */}
        {loading && (
          <>
            <Spinner className="mt-3" />
            <ProgressBar now={progress} label={`${progress}%`} className="mt-2" />
          </>
        )}

        {/* DOWNLOAD */}
        {resultUrl && (
          <a href={resultUrl} download className="btn btn-success mt-3">
            Download File
          </a>
        )}

        {/* PREVIEW */}
        {resultUrl && (
          <div className="mt-4">
            <iframe
              src={resultUrl}
              width="100%"
              height="500px"
              title="Preview"
              style={{ border: "1px solid #ccc" }}
            />
          </div>
        )}
      </Card>
    </Container>
  );
}