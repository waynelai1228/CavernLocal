import React from "react";
import "./TaskActionsPanel.css";

interface TaskActionsPanelProps {
  onCreateNewTask: () => void;
  // Add more props as needed for other buttons/actions
}

export default function TaskActionsPanel({ onCreateNewTask }: TaskActionsPanelProps) {
  return (
    <div className="task-actions-panel">
      <button onClick={onCreateNewTask}>+ Create New Task</button>
      {/* Future actions can go here */}
      {/* <button>Delete Selected Task</button> */}
      {/* <button>Export Tasks</button> */}
    </div>
  );
}
