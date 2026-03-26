import { Container, Row, Col } from "react-bootstrap";
import ToolCard from "../components/toolcard";

const Home = () => {
  return (
    <Container>

      <h2 className="text-center mt-4">All PDF Tools</h2>

      <h4 className="section-title">PDF Conversion</h4>
      <Row>
        <Col md={4}><ToolCard title="PDF to Word" path="/pdf-to-word" /></Col>
        <Col md={4}><ToolCard title="Word to PDF" path="/word-to-pdf" /></Col>
        <Col md={4}><ToolCard title="Excel to PDF" path="/excel-to-pdf" /></Col>
      </Row>

      <h4 className="section-title">PDF Editing</h4>
      <Row>
        <Col md={4}><ToolCard title="Merge PDF" path="/merge" /></Col>
        <Col md={4}><ToolCard title="Split PDF" path="/split" /></Col>
        <Col md={4}><ToolCard title="Rotate PDF" path="/rotate" /></Col>
      </Row>

      <h4 className="section-title">PDF Security</h4>
      <Row>
        <Col md={6}><ToolCard title="Lock PDF" path="/lock" /></Col>
        <Col md={6}><ToolCard title="Unlock PDF" path="/unlock" /></Col>
      </Row>

      <h4 className="section-title">Optimization</h4>
      <Row>
        <Col md={4}><ToolCard title="Compress PDF" path="/compress" /></Col>
      </Row>

    </Container>
  );
};

export default Home;