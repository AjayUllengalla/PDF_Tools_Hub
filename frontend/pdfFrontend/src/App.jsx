import { BrowserRouter, Routes, Route } from "react-router-dom";
import NavBar from "./components/navbar";
import Home from "./pages/home";

// Import all pages
import Merge from "./pages/merge";
import Split from "./pages/split";
import Compress from "./pages/compress";
import Rotate from "./pages/rotate";
import Lock from "./pages/lock";
import Unlock from "./pages/unlock";
import PdfToWord from "./pages/pdftoWord";
import WordToPdf from "./pages/wordtoPdf";
import ExcelToPdf from "./pages/exceltopdf";
import Footer from "./components/footer";

function App() {
  return (
    <BrowserRouter>
      <NavBar />

      <Routes>
        <Route path="/" element={<Home />} />

        {/* PDF Editing */}
        <Route path="/merge" element={<Merge />} />
        <Route path="/split" element={<Split />} />
        <Route path="/rotate" element={<Rotate />} />

        {/* Optimization */}
        <Route path="/compress" element={<Compress />} />

        {/* Security */}
        <Route path="/lock" element={<Lock />} />
        <Route path="/unlock" element={<Unlock />} />

        {/* Conversion */}
        <Route path="/pdf-to-word" element={<PdfToWord />} />
        <Route path="/word-to-pdf" element={<WordToPdf />} />
        <Route path="/excel-to-pdf" element={<ExcelToPdf />} />

      </Routes>

      <Footer/>
    </BrowserRouter>
  );
}

export default App;