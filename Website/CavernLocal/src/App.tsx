import './App.css'
import { Link, Routes, Route } from "react-router"
import New_Project from "./pages/new_project"
import Open_Project from "./pages/open_project"
function App() {

  return (
    <>
      <div className="landing">
        <Routes>
          <Route path="/new_project" element={<New_Project />} />
          <Route path="/open_project" element={<Open_Project />} />
        </Routes>
        <Link to="/new_project">
          <button>New Project</button>
        </Link>
        <Link to="/open_project">
          <button>Open Project</button>
        </Link>
      </div>
    </>
  );
}

export default App
