import {
    type RouteConfig,
    route,
} from "@react-router/dev/routes";

export default [
  // * matches all URLs, the ? makes it optional so it will match / as well
  route("/new_project", "./pages/new_project.tsx"),
  route("/open_project", "./pages/open_project.tsx"),
  route("/projects/:projectId", "./pages/project_detail.tsx"),
  route("*?", "catchall.tsx"),
] satisfies RouteConfig;