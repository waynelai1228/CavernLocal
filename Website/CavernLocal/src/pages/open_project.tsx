import "./form_pages.css";
import config from "../config";
import { useNavigate } from 'react-router';

// clientLoader with error handling
export async function clientLoader() {
  try {
    const res = await fetch(`${config.API_BASE_URL}/projects`);

    console.log(res.status);
    console.log(res.statusText);

    if (!res.ok) {
      throw new Error(`Failed to fetch projects: ${res.status} ${res.statusText}`);
    }

    const projects = await res.json();

    return {
      title: "Open Project",
      projects,
      error: null,
    };
  } catch (err) {
    console.error("Error fetching projects:", err);
    return {
      title: "Open Project",
      projects: [],
      error: err.message || "Unknown error",
    };
  }
}

// Component
export default function Open_Project({ loaderData }) {
  const { title, projects, error } = loaderData;
  const navigate = useNavigate();

  const handleRowClick = (projectId) => {
    // Navigate to the project detail page using the project ID
    navigate(`/projects/${projectId}`);
    console.log(`/project/${projectId}`);
  };


  return (
    <>
      <div className="form-page">
        <div className="form-heading">
          <h1>{title}</h1>
        </div>
        <div className="form-content">
          {error ? (
            <p style={{ color: 'red' }}>Error loading projects: {error}</p>
          ) : (
              <table className="project-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Project Name</th>
                  </tr>
                </thead>
                <tbody>
                  {projects.map((project) => (
                    <tr
                      key={project.id}
                      onClick={() => handleRowClick(project.id)}
                    >
                      <td>{project.id}</td>
                      <td>{project.project_name}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
        </div>
      </div>
    </>
  );
}