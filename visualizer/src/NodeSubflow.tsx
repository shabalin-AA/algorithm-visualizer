import { Node, Position, NodeProps, Handle, useReactFlow, NodeResizer } from "@xyflow/react";
import "./styles.css";
import LimitedConnectionHandle from "./LimitedConnectionHandle";

type Subflow = Node<{ code: string; result: string; selected: boolean }>;

export default function NodeSubflow({ id, data, selected }: NodeProps<Subflow>) {
    const { updateNodeData } = useReactFlow();
    return (
        <>
            <NodeResizer minHeight={40} minWidth={100} isVisible={selected} />
            <div
                style={{
                    width: "100%",
                    height: "100%",
                    minWidth: "100px",
                    minHeight: "40px",
                    border: "1px solid black",
                }}
            >
                <textarea
                    className="NodeSubflow-code"
                    value={data.code}
                    style={{ textAlign: "center" }}
                ></textarea>
                <div className="subflow-line" style={{ left: "10px" }}></div>
                <div className="subflow-line" style={{ right: "8px" }}></div>
            </div>
            {data.result !== "" && (
                <div className="node-result" onChange={(evt) => updateNodeData(id, {})}>
                    {data.result}
                </div>
            )}
            <Handle type="target" position={Position.Top} />
            <LimitedConnectionHandle type="source" position={Position.Bottom} connectioncount={1} />
        </>
    );
}
