import React, { useState, useEffect } from "react";
import './TaskDetail.css';
import config from "../config";
import { Task } from "../types/models"


export default function TaskDetail({
  task,
  onTaskSaved,
  projectId,
}: {
  task: Task | null;
  onTaskSaved?: (task: Task) => void;
  projectId: string;
}) {
  const [taskName, setTaskName] = useState(task?.task_name || "");
  const [taskDescription, setTaskDescription] = useState(task?.task_description || "");
  const [taskAction, setTaskAction] = useState(task?.task_action || "");
  const [taskType, setTaskType] = useState(task?.task_type || "BASH"); // or your default

  useEffect(() => {
    if (task) {
      setTaskName(task.task_name);
      setTaskDescription(task.task_description || "");
      setTaskAction(task.task_action || "");
      setTaskType(task.task_type || "BASH");
    } else {
      // clear form for new task
      setTaskName("");
      setTaskDescription("");
      setTaskAction("");
      setTaskType("BASH");
    }
  }, [task]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    // POST to backend to create task
    try {
      const res = await fetch(`${config.API_BASE_URL}/projects/${projectId}/tasks`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          task_name: taskName,
          task_description: taskDescription,
          task_action: taskAction,
          task_type: taskType,
        }),
      });

      if (!res.ok) throw new Error(`Failed to create task: ${res.status}`);

      const createdTask = await res.json(); 
      if (onTaskSaved) onTaskSaved(createdTask);;
    } catch (err) {
      alert("Error creating task: " + (err instanceof Error ? err.message : "Unknown"));
    }
  }

  if (!task) {
    // New Task form
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

        <button type="submit">Save Task</button>
      </form>

    );
  }

  // Existing task details display/edit logic goes here when task != null
  return (
    <div className="task-detail">
      <h2>{task.task_name}</h2>
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
