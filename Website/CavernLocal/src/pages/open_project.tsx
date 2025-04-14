export async function clientLoader() {
    return {
        title: "Open Project",
    }
}

export default function Open_Project({ loaderData }) {
    return <h1>{loaderData.title}</h1>;
}