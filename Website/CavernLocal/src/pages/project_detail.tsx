// src/pages/project_detail.tsx
import { useState, useEffect } from "react";
import { useParams } from "react-router"; // or use react-router-dom
import "./form_pages.css";
import config from "../config";

interface Project {
  id: string;
  projectName: string;
}
function getErrorMessage(error: unknown): string {
  if (!error) return "";
  if (typeof error === "string") return error;
  if (error instanceof Error) return error.message;
  return "Unknown error";
}

// Called on client-side navigation to fetch project data:
export async function clientLoader({
  params,
}: {
  params: { projectId?: string };
  }) {

  try {
    const { projectId } = params;
    if (!projectId) {
      throw new Error("Project ID is required");
    }

    const res = await fetch(`${config.API_BASE_URL}/projects/${projectId}`);
    if (!res.ok) {
      throw new Error(`Error fetching project: ${res.status}`);
    }

    const project: Project = await res.json();
    return {
      title: "Project Detail",
      project,
      error: null,
    };
  } catch (err) {
    console.error("Error fetching projects:", err);
    return {
      title: "Error Fetching Project",
      project: null,
      error: err.message || "Unknown error",
    };
  }
}

export default function FormComponent({ loaderData }:
  {
    loaderData: { title: string; project: Project, error: unknown };
  }
) {
  const { projectId } = useParams<{ projectId: string }>();
  const { title, project, error } = loaderData;
  const [errorMessage, setErrorMessage] = useState<string>(getErrorMessage(error));

  // Update errorMessage whenever loaderData.error changes
  useEffect(() => {
    setErrorMessage(getErrorMessage(error));
  }, [error]);

  if (errorMessage) {
    return <div className="error">Error: {errorMessage}</div>;
  }

  if (!project) {
    return <div>Loading project...</div>;
  }

  return (
    <div className="form-page">
      <h1>{title}</h1>
      <div className="project-detail">
        <label>ID:</label>
        <span>{project.id}</span>
      </div>
      <div className="project-detail">
        <label>Project Name:</label>
        <span>{project.projectName}</span>
      </div>
    </div>
  );
}