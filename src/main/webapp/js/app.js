class TaskList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {tasks: []};
    }

    componentDidMount() {
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
                <h4>Task List</h4>
                <div>
                    <button onClick={this.props.openCreateNewTaskForm}/>
                </div>
                {this.state.tasks.map(task => (
                    <Task task={task} />
                ))}
            </div>
        );
    }
}

class Task extends React.Component {
    render() {
        return (
            <div>
                <h5>{this.props.task.summary}</h5>
                <div>{this.props.task.description}</div>
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
