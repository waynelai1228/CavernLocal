import React from "react";
import "./TaskActionsPanel.css";

interface TaskActionsPanelProps {
  onCreateNewTask: () => void;
  onExportTasks: () => void;
  onImportTasks: () => void;
  onOpenEnvVars: () => void;
}

export default function TaskActionsPanel({
  onCreateNewTask,
  onExportTasks,
  onImportTasks,
  onOpenEnvVars,
}: TaskActionsPanelProps) {
  return (
    <div className="task-actions-panel">
      <button onClick={onCreateNewTask}>+ Create New Task</button>
      <button onClick={onImportTasks}>Import Tasks</button>
      <button onClick={onExportTasks}>Export Tasks</button>
      <button onClick={onOpenEnvVars}>Environment Variables</button>
    </div>
  );
}
