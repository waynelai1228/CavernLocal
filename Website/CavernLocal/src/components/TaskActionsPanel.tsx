import React from "react";
import "./TaskActionsPanel.css";

interface TaskActionsPanelProps {
  onCreateNewTask: () => void;
  onExportTasks: () => void;
  onImportTasks: () => void;
  // Add more props as needed for other buttons/actions
}

export default function TaskActionsPanel({
  onCreateNewTask,
  onExportTasks,
  onImportTasks,
}: TaskActionsPanelProps) {
  return (
    <div className="task-actions-panel">
      <button onClick={onCreateNewTask}>+ Create New Task</button>
      <button onClick={onImportTasks}>Import Tasks</button>
      <button onClick={onExportTasks}>Export Tasks</button>
    </div>
  );
}
