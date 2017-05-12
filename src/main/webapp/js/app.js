$(".modal").on("shown.bs.modal", function () { // any time a modal is shown
    const urlReplace = "#" + $(this).attr('id'); // make the hash the id of the modal shown
    history.pushState(null, null, urlReplace); // push state that hash into the url
});

// If a pushstate has previously happened and the back button is clicked, hide any modals.
$(window).on('popstate', function () {
    $(".modal").modal('hide');
});

function handleInputChange(event) {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;
    this.setState({[name]: value});
}

function updateTaskList() {
    let tasks = {};
    $.ajax({
        url: "/tasks",
        success: result => result.map(item => tasks[item.ID] = item),
        error: (xhr, textStatus, errorThrown) => {
            alert(textStatus);
        }
    });
    this.setState({tasks: tasks})
}

function preventDefault(e) {
    e.preventDefault();
}

class TaskList extends React.Component {
    componentDidMount() {
        Object.keys(this.props.tasks).map(key => {
            alert(this.props.tasks[key]);
        });
    }
    render() {
        return (
            <div>
                {Object.keys(this.props.tasks).map(key => {
                    return (
                        <Task task={this.props.tasks[key]} removeTask={this.props.removeTask}/>
                    );
                })}
            </div>
        );
    }
}

class TaskCreateForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {summary: "", description: ""};
        this.handleInputChange = handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit() {
        $.ajax({
            url: "/tasks",
            type: "POST",
            data: {
                summary: this.state.summary,
                description: this.state.description
            },
            success: result => {
                $("#taskCreateFormModal").modal("hide");
                $.ajax({
                    url: "/tasks?id=" + encodeURIComponent(result.ID),
                    success: res => {
                        this.props.addTask(res.task);
                    }
                });
            },
            error: (xhr, textStatus, errorThrown) => {
                console.log(errorThrown + textStatus);
                console.log(xhr);
                const err = JSON.parse(xhr.responseText);
                alert("Error: " + err.error);
            }
        });
    }

    render() {
        return (
            <div id="taskCreateFormModal" className="modal fade" role="dialog" data-keyboard="true">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <button type="button" className="close" data-dismiss="modal">&times;</button>
                            <h4 className="modal-title">Add New Task</h4>
                        </div>
                        <div className="modal-body">
                            <form>
                                <div className="form-group">
                                    <label htmlFor="summary">Summary:</label>
                                    <input name="summary"
                                           type="text"
                                           className="form-control"
                                           value={this.state.summary}
                                           onChange={this.handleInputChange}/>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="description">Description:</label>
                                    <textarea name="description"
                                              className="form-control"
                                              value={this.state.description}
                                              onChange={this.handleInputChange}/>
                                </div>
                            </form>
                        </div>
                        <div className="modal-footer">
                            <button type="button"
                                    className="btn btn-primary"
                                    onClick={this.handleSubmit}>
                                <span className="glyphicon glyphicon-ok"/> Create
                            </button>
                            <button type="button"
                                    className="btn"
                                    data-dismiss="modal">
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

class Task extends React.Component {
    constructor(props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete(e) {
        e.preventDefault();
        const id = this.props.task.ID;
        $.ajax({
            url: "/tasks?id=" + encodeURIComponent(id),
            type: "DELETE",
            success: result => {
                this.props.removeTask(id);
            },
            error: (xhr, textStatus, errorThrown) => {
                console.log(errorThrown + textStatus);
                console.log(xhr);
                const err = JSON.parse(xhr.responseText);
                alert("Error: " + err.error);
            }
        });
    }

    render() {
        return (
            <div className="panel panel-default">
                <div className="panel-heading">
                    <span className="badge badge-primary" style={{marginRight: "8px"}}>#{this.props.task.ID}</span>
                    <strong>{this.props.task.SUMMARY}</strong>
                    <div className="pull-right">
                        <div className="dropdown">
                            <a className="dropdown-toggle" href="#" data-toggle="dropdown">
                                Actions <span className="caret"/>
                            </a>
                            <ul className="dropdown-menu dropdown-menu-right">
                                <li><a href="#" onClick={this.handleDelete}>Delete Task</a></li>
                                <li><a href="#">...</a></li>
                                <li><a href="#">...</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div className="panel-body">{this.props.task.DESCRIPTION}</div>
            </div>
        );
    }
}

class NavBar extends React.Component {
    render() {
        return (
            <nav className="navbar navbar-inverse">
                <div className="container-fluid">
                    <div className="navbar-header">
                        <a className="navbar-brand" href="#">Task List</a>
                    </div>
                    <ul className="nav navbar-nav">
                        <li className="active"><a href="#">Home</a></li>
                        <li><a href="#">Page 1</a></li>
                        {/*<li><a href="#">Page 2</a></li>*/}
                    </ul>
                    <form className="navbar-form navbar-left">
                        <div className="input-group br">
                            <input type="text" className="form-control" placeholder="Search"/>
                            <div className="input-group-btn">
                                <div className="btn-group">
                                    <button className="btn btn-default" type="submit">
                                        <i className="glyphicon glyphicon-search"/>
                                    </button>
                                    <button className="btn btn-danger"
                                            data-toggle="modal"
                                            data-target="#taskCreateFormModal"
                                            onClick={preventDefault}>
                                        <span className="glyphicon glyphicon-plus"/> Create
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                    <ul className="nav navbar-nav navbar-right">
                        <li><a href="#"><span className="glyphicon glyphicon-user"/> Sign Up</a></li>
                        <li><a href="#"><span className="glyphicon glyphicon-log-in"/> Login</a></li>
                    </ul>
                </div>
            </nav>
        );
    }
}

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {tasks: {}};
        this.updateTaskList = updateTaskList.bind(this);
        this.addTask = this.addTask.bind(this);
    }

    componentDidMount() {
        this.updateTaskList();
    }

    addTask(task) {
        this.state.tasks[task.ID] = task;
        this.setState({tasks: this.state.tasks});
    }

    removeTask(id) {
        this.state.tasks.delete(id);
        this.setState({tasks: this.state.tasks});
    }

    render() {
        return (
            <div>
                <NavBar/>
                <TaskList tasks={this.state.tasks} removeTask={this.removeTask}/>
                <TaskCreateForm addTask={this.addTask}/>
            </div>
        );
    }
}

ReactDOM.render(<App/>, document.getElementById("root"));
