class TaskList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {tasks: []};
    }

    componentDidMount() {
        let self = this;
        $.ajax({
            url: "/tasks",
            success: result => self.setState({tasks: result})
        });
    }

    render() {
        return (
            <div>
                <div>
                    <h2>Task List
                        <button
                            style={{marginLeft: "8px"}}
                            className="btn btn-default"
                            data-toggle="modal" data-target="#taskCreateFormModal">
                            <span className="glyphicon glyphicon-plus"/>
                        </button>
                    </h2>
                </div>
                {this.state.tasks.map(task => (
                    <Task task={task}/>
                ))}
            </div>
        );
    }
}

class TaskCreateForm extends React.Component {
    render() {
        return (
            <div id="taskCreateFormModal" className="modal fade" role="dialog">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <button type="button" className="close" data-dismiss="modal">&times;</button>
                            <h4 className="modal-title">Modal Header</h4>
                        </div>
                        <div className="modal-body">
                            <p>Some text in the modal.</p>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

class Task extends React.Component {
    render() {
        return (
            <div className="panel panel-default">
                <div className="panel-heading">
                    <span className="badge badge-primary" style={{marginRight: "8px"}}>#{this.props.task.ID}</span>
                    <strong>{this.props.task.SUMMARY}</strong>
                </div>
                <div className="panel-body">{this.props.task.DESCRIPTION}</div>
            </div>
        );
    }
}

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {name: "", greeting: ""};
        this.changeName = this.changeName.bind(this);
        this.submitClick = this.submitClick.bind(this);
        this.openCreateNewTaskForm = this.openCreateNewTaskForm.bind(this);
    }

    openCreateNewTaskForm(e) {
        e.preventDefault();
        alert("Zz")
    }

    changeName(e) {
        this.setState({name: e.target.value});
    }

    submitClick(e) {
        e.preventDefault();
        let self = this;
        $.ajax({
            url: "/hello",
            data: {
                name: this.state.name
            },
            success: function (result) {
                self.setState({greeting: result.msg});
            }
        });
    }

    render() {
        return (
            <div>
                <TaskList openCreateNewTaskForm={this.openCreateNewTaskForm}/>
                <TaskCreateForm />
                {/*<form onSubmit={this.submitClick}>*/}
                {/*<label>Кто ты?<br/>*/}
                {/*<input type="text"*/}
                {/*value={this.state.name}*/}
                {/*onChange={this.changeName} />*/}
                {/*</label>*/}
                {/*<br/>*/}
                {/*<input type="submit" value="Отправить на сервер"/>*/}
                {/*</form>*/}
                {/*<Greeting greeting={this.state.greeting}/>*/}
            </div>
        );
    }
}

ReactDOM.render(<App/>, document.getElementById("root"));
