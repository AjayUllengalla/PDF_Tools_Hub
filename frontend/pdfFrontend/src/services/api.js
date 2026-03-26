import axios from "axios";

const API = "http://localhost:8080/pdf/convert";

export const uploadFiles = async (endpoint, files, setProgress) => {
  const formData = new FormData();

  files.forEach(file => formData.append("file", file));

  const res = await axios.post(`${API}/${endpoint}`, formData, {
    onUploadProgress: (data) => {
      const percent = Math.round((data.loaded * 100) / data.total);
      setProgress(percent);
    }
  });

  return res.data;
};