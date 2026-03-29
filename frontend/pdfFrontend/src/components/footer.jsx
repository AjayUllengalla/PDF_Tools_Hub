import { Container, Row, Col } from "react-bootstrap";
import { Link } from "react-router-dom";

const Footer = () => {
  return (
    <footer
      style={{
        background: "linear-gradient(90deg, #232526, #414345)",
        color: "white",
        marginTop: "20px",
        padding: "50px 0"
      }}
    >
      <Container>
        <Row>

          
          <Col md={4} className="mb-4">
            <h4 className="fw-bold">PDF Tools Hub</h4>
            <p style={{ fontSize: "14px", color: "#ccc" }}>
              Free online tools to merge, split, compress, convert and secure your PDF files easily.
            </p>
          </Col>

          
          <Col md={2} className="mb-4">
            <h6 className="fw-bold">Quick Links</h6>
            <ul className="list-unstyled">
              <li><Link to="/" className="footer-link">Home</Link></li>
              <li><Link to="/merge" className="footer-link">Merge PDF</Link></li>
              <li><Link to="/split" className="footer-link">Split PDF</Link></li>
              <li><Link to="/compress" className="footer-link">Compress PDF</Link></li>
            </ul>
          </Col>

          
          <Col md={3} className="mb-4">
            <h6 className="fw-bold">Tools</h6>
            <ul className="list-unstyled">
              <li><Link to="/pdf-to-word" className="footer-link">PDF to Word</Link></li>
              <li><Link to="/word-to-pdf" className="footer-link">Word to PDF</Link></li>
              <li><Link to="/excel-to-pdf" className="footer-link">Excel to PDF</Link></li>
              <li><Link to="/lock" className="footer-link">Lock PDF</Link></li>
            </ul>
          </Col>

          
          <Col md={3} className="mb-4">
            <h6 className="fw-bold">Contact</h6>
            <p style={{ fontSize: "14px", color: "#ccc" }}>
              Email: support@pdftoolshub.com
            </p>

            {/* <div className="d-flex gap-3">
              <i className="bi bi-facebook"></i>
              <i className="bi bi-twitter"></i>
              <i className="bi bi-linkedin"></i>
            </div> */}
          </Col>

        </Row>

        <hr style={{ borderColor: "#666" }} />

        <p className="text-center mb-0" style={{ fontSize: "13px", color: "#aaa" }}>
          © 2026 PDF Tools Hub. All rights reserved.
        </p>
      </Container>
    </footer>
  );
};

export default Footer;