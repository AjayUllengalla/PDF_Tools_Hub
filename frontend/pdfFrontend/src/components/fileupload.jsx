import { useState } from "react";

const FileUpload = ({ setFiles }) => {
  const [preview, setPreview] = useState([]);

  const handleChange = (e) => {
    const files = Array.from(e.target.files);
    setFiles(files);

    const urls = files.map(file => URL.createObjectURL(file));
    setPreview(urls);
  };

  return (
    <div>
      <label className="upload-box">
        <input
          type="file"
          hidden
          multiple
          onChange={handleChange}
        />
        <h5>Click or Drag & Drop PDF here</h5>
      </label>

      <div className="preview-box mt-3 d-flex gap-3 flex-wrap">
        {preview.map((src, i) => (
          <iframe key={i} src={src} width="120" height="140"></iframe>
        ))}
      </div>
    </div>
  );
};

export default FileUpload;