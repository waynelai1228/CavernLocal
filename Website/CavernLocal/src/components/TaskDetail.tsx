import React, { useState, useEffect } from "react";
import './TaskDetail.css';
import config from "../config";
import { Task } from "../types/models";

export default function TaskDetail({
  task,
  onTaskSaved,
  onEditTask,
  onDeleteTask,
  isEditing,
  projectId,
}: {
  task: Task;
  onTaskSaved?: (task: Task) => void;
  onEditTask?: () => void;
  onDeleteTask?: (taskId: string) => void;
  isEditing: boolean;
  projectId: string;
}) {
  const [taskName, setTaskName] = useState(task.task_name);
  const [taskDescription, setTaskDescription] = useState(task.task_description || "");
  const [taskAction, setTaskAction] = useState(task.task_action || "");
  const [taskType, setTaskType] = useState(task.task_type || "BASH");

  useEffect(() => {
    setTaskName(task.task_name);
    setTaskDescription(task.task_description || "");
    setTaskAction(task.task_action || "");
    setTaskType(task.task_type || "BASH");
  }, [task]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    try {
      const res = await fetch(`${config.API_BASE_URL}/projects/${projectId}/tasks/${task.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          task_name: taskName,
          task_description: taskDescription,
          task_action: taskAction,
          task_type: taskType,
        }),
      });

      if (!res.ok) throw new Error(`Failed to update task: ${res.status}`);

      const updatedTask = await res.json();
      if (onTaskSaved) onTaskSaved(updatedTask);
    } catch (err) {
      alert("Error updating task: " + (err instanceof Error ? err.message : "Unknown"));
    }
  }

  function handleRunClick() {
    // Optional: API call or local state update
    console.log("Running task:", task.id);
  }

  if (isEditing) {
    return (
      <form className="edit-task-form task-form" onSubmit={handleSubmit}>
        <label htmlFor="taskName">Task Name</label>
        <input
          type="text"
          id="taskName"
          name="taskName"
          value={taskName}
          onChange={(e) => setTaskName(e.target.value)}
        />

        <label htmlFor="taskDescription">Description</label>
        <textarea
          id="taskDescription"
          name="taskDescription"
          value={taskDescription}
          onChange={(e) => setTaskDescription(e.target.value)}
        />

        <label htmlFor="taskAction">Action</label>
        <input
          type="text"
          id="taskAction"
          name="taskAction"
          value={taskAction}
          onChange={(e) => setTaskAction(e.target.value)}
        />

        <label htmlFor="taskType">Type</label>
        <select
          id="taskType"
          name="taskType"
          value={taskType}
          onChange={(e) => setTaskType(e.target.value)}
        >
          <option value="BASH">BASH</option>
          <option value="OTHER">OTHER</option>
        </select>

        <button type="submit">Save Changes</button>
      </form>
    );
  }

  // Viewing mode
  return (
    <div className="task-detail">
      <div className="task-header">
        <h2>{task.task_name}</h2>
        <div className="task-header-buttons">
          <button className="task-button edit" onClick={onEditTask}>
            Edit Task
          </button>
          <button
            className="task-button delete"
            onClick={() => {
              if (window.confirm("Are you sure you want to delete this task?")) {
                onDeleteTask?.(task.id);
              }
            }}
          >
            Delete Task
          </button>
        </div>
      </div>

      <div className="task-description">{task.task_description}</div>

      <section className="task-action-result">
        <div className="task-action-header">
          <h3>Task Action</h3>
          <button className="task-button run" onClick={handleRunClick}>Run</button>
        </div>

        <div className="task-result-header">
          <h3>Task Result</h3>
        </div>

        <pre className="task-action-code"><code>{task.task_action}</code></pre>
        <pre className="task-result-code"><code>{task.task_result || "No result yet."}</code></pre>
      </section>
    </div>
  );
}
