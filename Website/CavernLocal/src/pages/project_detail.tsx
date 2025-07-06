// src/pages/project_detail.tsx
import { useState, useEffect } from "react";
import { useParams } from "react-router"; // or use react-router-dom
import "./detail_pages.css";
import config from "../config";
import TaskDetail from "../components/TaskDetail";
import TaskTile from "../components/TaskTile"

interface Project {
  id: string;
  project_name: string;
}
interface Task {
  id: string;
  task_name: string;
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

const tasks = [
  { id: 1, name: 'Task One', description: 'This is task one.' },
  { id: 2, name: 'Task Two', description: 'This is task two.' },
  { id: 3, name: 'Task Three', description: 'This is task three.' },
  { id: 4, name: 'Task Four', description: 'This is task four.' },
  // Add more as needed
];

export default function FormComponent({ loaderData }:
  {
    loaderData: { title: string; project: Project, tasks: Task; error: unknown };
  }
) {
  const { projectId } = useParams<{ projectId: string }>();
  const { title, project, error } = loaderData;
  const [errorMessage, setErrorMessage] = useState<string>(getErrorMessage(error));

  const [selectedTask, setSelectedTask] = useState(null);

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
    <div className="container">
      <div className="sidebar">

        <div className="project-header">
          <div className="project-name">{project.project_name}</div>
          <div className="project-id">Project ID: {project.id}</div>
        </div>

        {tasks.map((task) => (
          <TaskTile
            key={task.id}
            task={task}
            isSelected={selectedTask?.id === task.id}
            onClick={() => setSelectedTask(task)}
          />
        ))}
      </div>
      <div className="details">
        <TaskDetail task={selectedTask} />
      </div>
    </div>
  );
}