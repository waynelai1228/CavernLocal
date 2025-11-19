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

type Mode = "none" | "create" | "view" | "edit";

export default function FormComponent({ loaderData }:
  {
    loaderData: { title: string; project: Project, tasks: Task[]; error: unknown };
  }
) {
  const { title, project, error } = loaderData;
  const [tasks, setTasks] = useState<Task[]>(loaderData.tasks);
  const [errorMessage, setErrorMessage] = useState<string>(getErrorMessage(error));

  const [mode, setMode] = useState<Mode>("none");
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);

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
  function selectTask(task: Task) {
    setSelectedTask(task);
    setMode("view");
  }

  function createNewTask() {
    setSelectedTask(null);
    setMode("create");
  }

  function startEdit() {
    if (selectedTask) setMode("edit");
  }

  // Callback to exit new task creation
  function handleTaskSaved(newTask: Task) {
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

    // select & switch back to view mode
    setSelectedTask(newTask);
    setMode("view");
  }

  async function handleDeleteTask(taskId: string) {
    try {
      const res = await fetch(`${config.API_BASE_URL}/projects/${project.id}/tasks/${taskId}`, {
        method: "DELETE",
      });

      if (!res.ok) {
        throw new Error(`Failed to delete task: ${res.status}`);
      }

      setTasks(prev => prev.filter(task => task.id !== taskId));

      // Clear selection & mode
      setSelectedTask(null);
      setMode("none");
    } catch (err) {
      setErrorMessage(getErrorMessage(err));
    }
  }

  async function handleImportTasks() {
    try {
      const input = document.createElement("input");
      input.type = "file";
      input.accept = "application/json";

      input.onchange = async (event) => {
        const file = (event.target as HTMLInputElement).files?.[0];
        if (!file) return;

        const text = await file.text();
        let importedTasks: Partial<Task>[];

        try {
          importedTasks = JSON.parse(text);
        } catch {
          alert("Invalid JSON file format.");
          return;
        }

        if (!Array.isArray(importedTasks)) {
          alert("File must contain an array of tasks.");
          return;
        }

        const createdTasks: Task[] = [];

        for (const t of importedTasks) {
          // Skip invalid entries
          if (!t.task_name || !t.task_action) continue;

          try {
            const res = await fetch(
              `${config.API_BASE_URL}/projects/${project.id}/tasks`,
              {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                  task_name: t.task_name,
                  task_description: t.task_description || "",
                  task_action: t.task_action,
                  task_type: t.task_type || "BASH",
                }),
              }
            );

            if (!res.ok) {
              console.warn(`Failed to import task "${t.task_name}": ${res.status}`);
              continue;
            }

            const newTask = (await res.json()) as Task;
            createdTasks.push(newTask);
          } catch (err) {
            console.error("Error creating task:", err);
          }
        }

        if (createdTasks.length > 0) {
          setTasks((prev) => [...prev, ...createdTasks]);
          alert(`Successfully imported ${createdTasks.length} tasks!`);
        } else {
          alert("No tasks were imported.");
        }
      };

      input.click();
    } catch (err) {
      setErrorMessage(getErrorMessage(err));
    }
  }



  async function handleExportTasks() {
    try {
      const res = await fetch(`${config.API_BASE_URL}/projects/${project.id}/export`);

      if (!res.ok) {
        throw new Error(`Failed to export tasks: ${res.status}`);
      }

      const data = await res.json();

      if (data.filePath) {
        // Optional: trigger file download if file is directly accessible
        const link = document.createElement("a");
        link.href = data.filePath;
        link.download = "tasks_export.json";
        link.click();
      } else {
        alert("Export successful, but no file path returned.");
      }
    } catch (err) {
      setErrorMessage(getErrorMessage(err));
    }
  }

  // -----------------------------
  //  RENDERING WITH UNIFIED MODE
  // -----------------------------

  let content = null;

  if (mode === "create") {
    content = (
      <CreateTaskForm projectId={project.id} onTaskSaved={handleTaskSaved} />
    );
  } else if (mode === "view" && selectedTask) {
    content = (
      <TaskDetail
        task={selectedTask}
        onTaskSaved={handleTaskSaved}
        onDeleteTask={handleDeleteTask}
        projectId={project.id}
        onEditTask={startEdit}
        isEditing={false}
      />
    );
  } else if (mode === "edit" && selectedTask) {
    content = (
      <TaskDetail
        task={selectedTask}
        onTaskSaved={handleTaskSaved}
        onDeleteTask={handleDeleteTask}
        projectId={project.id}
        onEditTask={() => { }}
        isEditing={true}
      />
    );
  } else {
    content = <div>Select a task or create a new one.</div>;
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
            onClick={() => selectTask(task)}
          />
        ))}
      </div>

      <div className="details">
        <TaskActionsPanel
          onCreateNewTask={createNewTask}
          onExportTasks={handleExportTasks}
          onImportTasks={handleImportTasks}
        />

        {content}
      </div>
    </div>
  );
}