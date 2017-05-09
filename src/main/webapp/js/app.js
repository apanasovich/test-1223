class TaskList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {tasks: []};
    }

    componentDidMount() {
        let self = this;
        $.ajax({
            url: "/tasks",
            success: function (result) {
                self.setState({tasks: result});
            }
        });
    }

    render() {
        return (
            <div>
                <div className="row">
                    <h2 className="col-sm-11">Task List</h2>
                    <button
                        style={{marginLeft: "8px", float: "right"}}
                        className="btn btn-primary col-sm-1 float-right"
                        onClick={this.props.openCreateNewTaskForm}>
                        <span className="glyphicon glyphicon-plus"/>
                    </button>
                </div>
                {this.state.tasks.map(task => (
                    <Task task={task}/>
                ))}
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
