import { Navbar, Nav, Container, NavDropdown } from "react-bootstrap";
import { Link } from "react-router-dom";

const NavBar = () => {
  return (
    <Navbar expand="lg" bg="white" className="shadow-sm py-3" sticky="top" text-white >
      <Container>
        <Navbar.Brand as={Link} to="/" style={{ fontWeight: "bold", color: "#6c63ff" }}>
           <span><img src="https://play-lh.googleusercontent.com/3kI0WZIkMKhVlPdMxBiwgEPiQKWZ-XApq_JOXgra7lpeSlwpg2NzphqtY9KI_IaEHsYb" alt="" height="37" width="37" style={{borderRadius:"14px"}}/></span> PDF Tools Hub
        </Navbar.Brand>

        <Navbar.Toggle />
        <Navbar.Collapse>
          <Nav className="ms-auto">

            {/* PDF Editing */}
            <NavDropdown title="Edit PDF" >
              <NavDropdown.Item as={Link} to="/merge">Merge PDF</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/split">Split PDF</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/rotate">Rotate PDF</NavDropdown.Item>
            </NavDropdown>

            {/* Optimization */}
            <NavDropdown title="Optimize">
              <NavDropdown.Item as={Link} to="/compress">Compress PDF</NavDropdown.Item>
            </NavDropdown>

            {/* Security */}
            <NavDropdown title="Security">
              <NavDropdown.Item as={Link} to="/lock">Lock PDF</NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/unlock">Unlock PDF</NavDropdown.Item>
            </NavDropdown>

            {/* Conversion */}
            <NavDropdown title="Convert">
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