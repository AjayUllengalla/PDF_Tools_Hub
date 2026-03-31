import { useState } from "react";
import { Navbar, Nav, Container, NavDropdown } from "react-bootstrap";
import { Link } from "react-router-dom";


const LogoSVG = () => (
  <svg
    width="38"
    height="38"
    viewBox="0 0 310 80"
    xmlns="http://www.w3.org/2000/svg"
  >
    <rect x="0" y="0" width="52" height="66" rx="7" fill="white" fillOpacity="0.95" />
    <polygon points="36,0 52,16 36,16" fill="white" fillOpacity="0.45" />
    <rect x="8" y="22" width="22" height="3.5" rx="1.5" fill="#764ba2" opacity="0.9" />
    <rect x="8" y="31" width="30" height="3.5" rx="1.5" fill="#764ba2" opacity="0.7" />
    <rect x="8" y="40" width="19" height="3.5" rx="1.5" fill="#764ba2" opacity="0.6" />
    <rect x="8" y="49" width="26" height="3.5" rx="1.5" fill="#764ba2" opacity="0.45" />
    <circle cx="44" cy="57" r="13" fill="#ff6b6b" />
    <circle cx="44" cy="57" r="4.5" fill="white" />
    <circle cx="44" cy="57" r="2" fill="#ff6b6b" />
  </svg>
);

const NavBar = () => {

  const [showEdit, setShowEdit] = useState(false);
  const [showOptimize, setShowOptimize] = useState(false);
  const [showSecurity, setShowSecurity] = useState(false);
  const [showConvert, setShowConvert] = useState(false);

  return (
    <Navbar
      expand="lg"
      className="shadow-sm"
      sticky="top"
      style={{
        background: "linear-gradient(90deg, #667eea, #764ba2)"
      }}
      variant="dark"
    >
      <Container>

        {/* ✅ Updated Brand */}
        <Navbar.Brand as={Link} to="/" className="fw-bold fs-4 d-flex align-items-center gap-2">
          <LogoSVG />
          PDF Tools Hub
        </Navbar.Brand>

        <Navbar.Toggle />
        <Navbar.Collapse>
          <Nav className="ms-auto">

            <NavDropdown title="Edit PDF"
              show={showEdit}
              onMouseEnter={() => setShowEdit(true)}
              onMouseLeave={() => setShowEdit(false)}
            >
              <NavDropdown.Item as={Link} to="/merge">Merge PDF</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/split">Split PDF</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/rotate">Rotate PDF</NavDropdown.Item>
            </NavDropdown>

            <NavDropdown title="Optimize"
              show={showOptimize}
              onMouseEnter={() => setShowOptimize(true)}
              onMouseLeave={() => setShowOptimize(false)}
            >
              <NavDropdown.Item as={Link} to="/compress">Compress PDF</NavDropdown.Item>
            </NavDropdown>

            <NavDropdown title="Security"
              show={showSecurity}
              onMouseEnter={() => setShowSecurity(true)}
              onMouseLeave={() => setShowSecurity(false)}
            >
              <NavDropdown.Item as={Link} to="/lock">Lock PDF</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/unlock">Unlock PDF</NavDropdown.Item>
            </NavDropdown>

            <NavDropdown title="Convert"
              show={showConvert}
              onMouseEnter={() => setShowConvert(true)}
              onMouseLeave={() => setShowConvert(false)}
            >
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
