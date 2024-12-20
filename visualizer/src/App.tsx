import Flow from "./Flow";
import TopBar from "./TopBar";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Doc from "./Doc";
import About from "./About";

export default function App() {
    return (
        <Router>
            <div className="App">
                <TopBar />
                <div style={{ marginTop: "40px", padding: "0px" }}>
                    <Routes>
                        <Route path="/" element={<Flow />} /> {}
                        <Route path="/doc" element={<Doc />} /> {}
                        <Route path="/about" element={<About />} /> {}
                    </Routes>
                </div>
            </div>
        </Router>
    );
}
