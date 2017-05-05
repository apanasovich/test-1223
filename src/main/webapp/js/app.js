class Toolbar extends React.Component {
    render() {
        return (
            <h3>Чё как, {this.props.name}?</h3>
        );
    }
}

class Greeting extends React.Component {
    render() {
        return (
            <h4>{this.props.greeting}</h4>
        );
    }
}

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {name: "", greeting: ""};
        this.changeName = this.changeName.bind(this);
        this.submitClick = this.submitClick.bind(this);
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
                <Toolbar name={this.state.name}/>
                <form onSubmit={this.submitClick}>
                    <label>Кто ты?<br/>
                        <input type="text"
                               value={this.state.name}
                               onChange={this.changeName} />
                    </label>
                    <br/>
                    <input type="submit" value="Отправить на сервер"/>
                </form>
                <Greeting greeting={this.state.greeting}/>
            </div>
        );
    }
}

ReactDOM.render(<App name="Zzz"/>, document.getElementById("root"));
