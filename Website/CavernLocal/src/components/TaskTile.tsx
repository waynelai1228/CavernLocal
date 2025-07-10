import React from 'react';

export default function TaskTile({ task, isSelected, onClick }) {
  return (
    <div
      className={`tile ${isSelected ? 'active' : ''}`}
      onClick={onClick}
    >
      {task.task_name}
    </div>
  );
}