export async function clientLoader() {
    return {
        title: "New Project",
    }
}

export default function New_Project({ loaderData }) {
    return <h1>{loaderData.title}</h1>;
}