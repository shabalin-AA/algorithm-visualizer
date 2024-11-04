import { Handle, Node, Position, NodeProps} from "@xyflow/react"
import "./styles.css"
import CustomHandle from "./CustomHandle"

type Text = Node<{label: string}>;

function CustomNodeInput ({data}: NodeProps<Text>) {
  return (
    <div>
        <div style={{
          width: "200px", 
          height: "30px",
          background: '#fff',
          border: '1px solid #777',
          }}>
            <input type="text" className="node-label" placeholder="diamond"></input>
        </div> 
        <div>
            <CustomHandle type="source" position={Position.Bottom} connectioncount={1}/>
        </div>   
    </div>
  );
};

export default CustomNodeInput;
