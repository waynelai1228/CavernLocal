export interface Project {
  id: string;
  project_name: string;
}
export interface Task {
  id: string;
  task_name: string;
  task_description?: string;
  task_action?: string;
  task_result?: string;
  task_type?: string;
}