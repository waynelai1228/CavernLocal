import "./form_pages.css";
import config from "../config";

// clientLoader with error handling
export async function clientLoader() {
  try {
    const res = await fetch(`${config.API_BASE_URL}/api/projects`);

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
            <ul>
              {projects.map(project => (
                <li key={project.id}>{project.name}</li>
              ))}
            </ul>
            )}
        </div>
      </div>
    </>
  );
}