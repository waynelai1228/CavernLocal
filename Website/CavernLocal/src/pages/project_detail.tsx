import { useState, useEffect } from "react";

import "./detail_pages.css";

import config from "../config";

import CreateTaskForm from "../components/CreateTaskForm";
import TaskActionsPanel from "../components/TaskActionsPanel";
import TaskDetail from "../components/TaskDetail";
import TaskTile from "../components/TaskTile";

import type { Project, Task } from "../types/models";

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

    // Fetch project
    const res = await fetch(`${config.API_BASE_URL}/projects/${projectId}`);
    if (!res.ok) {
      throw new Error(`Error fetching project: ${res.status}`);
    }
    const project: Project = await res.json();

    // Fetch tasks for the project
    const tasksRes = await fetch(`${config.API_BASE_URL}/projects/${projectId}/tasks`);
    if (!tasksRes.ok) {
      throw new Error(`Error fetching tasks: ${tasksRes.status}`);
    }
    const tasks: Task[] = await tasksRes.json();

    return {
      title: "Project Detail",
      project,
      tasks,
      error: null,
    };
  } catch (err) {
    console.error("Error fetching project or tasks:", err);
    return {
      title: "Error Fetching Project",
      project: null,
      tasks: [],
      error: err instanceof Error ? err.message : "Unknown error",
    };
  }
}

export default function FormComponent({ loaderData }:
  {
    loaderData: { title: string; project: Project, tasks: Task[]; error: unknown };
  }
) {
  const { title, project, error } = loaderData;
  const [tasks, setTasks] = useState<Task[]>(loaderData.tasks);
  const [errorMessage, setErrorMessage] = useState<string>(getErrorMessage(error));

  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [isCreatingNewTask, setIsCreatingNewTask] = useState(false);
  const [isEditingTask, setIsEditingTask] = useState(false);

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

  useEffect(() => {
    if (selectedTask !== null) {
      setIsCreatingNewTask(false);
      setIsEditingTask(false);
    }
  }, [selectedTask]);


  function handleCreateNewClick() {
    setSelectedTask(null);
    setIsEditingTask(false);
    setIsCreatingNewTask(true);
  }

  // Callback to exit new task creation
  function handleTaskSaved(newTask: Task) {
    setIsCreatingNewTask(false);

    setTasks(prev => {
      const exists = prev.some(task => task.id === newTask.id);
      if (exists) {
        // update the existing task (e.g., replace it)
        return prev.map(task => (task.id === newTask.id ? newTask : task));
      } else {
        // Append if not found
        return [...prev, newTask];
      }
    });

    setSelectedTask(newTask); // select the new or updated task
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
            onClick={() => {
              setIsCreatingNewTask(false); // ensure when a tile is selected, exit create mode
              setSelectedTask(task);
            }}
          />
        ))}
      </div>
      <div className="details">
        <TaskActionsPanel onCreateNewTask={handleCreateNewClick} />

        {isCreatingNewTask || !selectedTask ? (
          <CreateTaskForm
            projectId={project.id}
            onTaskSaved={handleTaskSaved}
          />
        ) : (
          <TaskDetail
            task={selectedTask}
            onTaskSaved={handleTaskSaved}
            projectId={project.id}
            onEditTask={() => setIsEditingTask(true)}
            isEditing={isEditingTask}
          />
        )}
      </div>
    </div>
  );
}