import { useState } from "react";
import { Navbar, Nav, Container, NavDropdown } from "react-bootstrap";
import { Link } from "react-router-dom";

const NavBar = () => {

  const [showEdit, setShowEdit] = useState(false);
  const [showOptimize, setShowOptimize] = useState(false);
  const [showSecurity, setShowSecurity] = useState(false);
  const [showConvert, setShowConvert] = useState(false);
  return (
    <Navbar
      expand="lg"
      className="shadow-sm" sticky="top"
      style={{
        background: "linear-gradient(90deg, #667eea, #764ba2)"
      }}
      variant="dark"
    >
      <Container>
        <Navbar.Brand as={Link} to="/" className="fw-bold fs-4">
           <span><img src="https://pdfworld.in/logo.png" alt="" height={38} width={38} style={{borderRadius:"10px"}} /></span> PDF Tools Hub
        </Navbar.Brand>

        <Navbar.Toggle />
        <Navbar.Collapse>
          <Nav className="ms-auto">

            <NavDropdown title="Edit PDF" menuVariant="light"  show={showEdit}
  onMouseEnter={() => setShowEdit(true)}
  onMouseLeave={() => setShowEdit(false)}>
              <NavDropdown.Item as={Link} to="/merge">Merge PDF</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/split">Split PDF</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/rotate">Rotate PDF</NavDropdown.Item>
            </NavDropdown>

            <NavDropdown title="Optimize" show={showOptimize}
  onMouseEnter={() => setShowOptimize(true)}
  onMouseLeave={() => setShowOptimize(false)} >
              <NavDropdown.Item as={Link} to="/compress">Compress PDF</NavDropdown.Item>
            </NavDropdown>

            <NavDropdown title="Security" show={showSecurity}
  onMouseEnter={() => setShowSecurity(true)}
  onMouseLeave={() => setShowSecurity(false)}>
              <NavDropdown.Item as={Link} to="/lock">Lock PDF</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/unlock">Unlock PDF</NavDropdown.Item>
            </NavDropdown>

            <NavDropdown title="Convert" show={showConvert}
  onMouseEnter={() => setShowConvert(true)}
  onMouseLeave={() => setShowConvert(false)}>
              <NavDropdown.Item as={Link} to="/pdf-to-word">PDF to Word</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/word-to-pdf">Word to PDF</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/excel-to-pdf">Excel to PDF</NavDropdown.Item>
            </NavDropdown>

          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default NavBar;
