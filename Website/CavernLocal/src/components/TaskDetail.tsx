import React from 'react';

export default function TaskDetails({ task }) {
  if (!task) {
    return <div><h2>Select a task to see details</h2></div>;
  }

  return (
    <div>
      <h2>{task.name}</h2>
      <p>{task.description}</p>
    </div>
  );
}