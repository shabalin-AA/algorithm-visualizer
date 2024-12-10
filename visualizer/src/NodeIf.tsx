import { Node, Position, NodeProps, Handle, useReactFlow, NodeResizer } from "@xyflow/react";
import "./styles.css";
import LimitedConnectionHandle from "./LimitedConnectionHandle";
import { useEffect, useState } from "react";

type Text = Node<{ code: string; result: string; selected: boolean }>;

export default function NodeIf({ id, data, selected }: NodeProps<Text>) {
    const { updateNodeData } = useReactFlow();
    const [code, setCode] = useState(data.code);

    const handleCodeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setCode(event.target.value);
    };

    useEffect(() => {
        setCode(data.code);
    }, [data.code]);

    return (
        <>
            <NodeResizer minHeight={100} minWidth={100} isVisible={selected} />
            <div style={{ width: "100%", height: "100%", minWidth: "100px", minHeight: "100px" }}>
                <svg
                    viewBox="0 0 100 100"
                    style={{
                        position: "absolute",
                        width: "100%",
                        height: "100%",
                        transform: "translate(-50%, 0%)",
                        scale: "inherit",
                    }}
                >
                    <polygon points="0,50 50,0 100,50 50,100" fill="white" stroke="black" />
                </svg>
                <input
                    value={code}
                    placeholder="a > b"
                    onChange={handleCodeChange}
                    className="NodeIf-code"
                    onBlur={() => {
                        updateNodeData(id, { code: code });
                    }}
                />
            </div>
            {data.result !== "" && (
                <div className="node-result" onChange={(evt) => updateNodeData(id, {})}>
                    {data.result}
                </div>
            )}
            <Handle type="target" position={Position.Top} />
            <LimitedConnectionHandle
                type="source"
                position={Position.Left}
                connectioncount={1}
                id="false"
            />
            <LimitedConnectionHandle
                type="source"
                position={Position.Right}
                connectioncount={1}
                id="true"
            />
        </>
    );
}
