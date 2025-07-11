import React, { useState, useEffect } from "react";
import './TaskDetail.css';
import config from "../config";
import { Task } from "../types/models";

export default function TaskDetail({
  task,
  onTaskSaved,
  onEditTask,
  isEditing,
  projectId,
}: {
  task: Task;
  onTaskSaved?: (task: Task) => void;
  onEditTask?: () => void;
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

  if (isEditing) {
    return (
      <form className="task-detail-form" onSubmit={handleSubmit}>
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
        <button className="edit-task-button" onClick={onEditTask}>
          Edit Task
        </button>
      </div>

      <div>{task.task_description}</div>

      <section className="task-action-result">
        <div className="task-action-section">
          <h3>Task Action</h3>
          <pre><code>{task.task_action}</code></pre>
        </div>

        <div className="task-result-section">
          <h3>Task Result</h3>
          <pre><code>{task.task_result || "No result yet."}</code></pre>
        </div>
      </section>
    </div>
  );
}
