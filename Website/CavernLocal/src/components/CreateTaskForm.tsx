import React, { useState } from "react";
import config from "../config";
import { Task } from "../types/models";

export default function CreateTaskForm({
  onTaskSaved,
  projectId,
}: {
  onTaskSaved: (task: Task) => void;
  projectId: string;
}) {
  const [taskName, setTaskName] = useState("");
  const [taskDescription, setTaskDescription] = useState("");
  const [taskAction, setTaskAction] = useState("");
  const [taskType, setTaskType] = useState("BASH");

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

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

      if (!res.ok) throw new Error("Failed to create task");

      const newTask: Task = await res.json();
      onTaskSaved(newTask);
    } catch (err) {
      alert("Error creating task: " + (err instanceof Error ? err.message : "Unknown error"));
    }
  }

  return (
    <form className="task-detail-form" onSubmit={handleSubmit}>
      <label htmlFor="taskName">Task Name</label>
      <input
        type="text"
        id="taskName"
        name="taskName"
        value={taskName}
        onChange={(e) => setTaskName(e.target.value)}
        required
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
        required
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

      <button type="submit">Create Task</button>
    </form>
  );
}
