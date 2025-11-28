# Contributing to Feed4j

Thank you for your interest in contributing to Feed4j! 🎉 We welcome contributions from everyone. This document provides guidelines and information for contributors.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Contributing Guidelines](#contributing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Testing](#testing)
- [Documentation](#documentation)
- [Reporting Issues](#reporting-issues)
- [License](#license)

## 🤝 Code of Conduct

This project follows a code of conduct to ensure a welcoming environment for all contributors. By participating, you agree to:

- Be respectful and inclusive
- Focus on constructive feedback
- Accept responsibility for mistakes
- Show empathy towards other contributors
- Help create a positive community

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Git

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/your-username/feed4j.git
   cd feed4j
   ```
3. Set up the upstream remote:
   ```bash
   git remote add upstream https://github.com/original-owner/feed4j.git
   ```

## 🛠️ Development Setup

### Build the Project

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Create JAR file
mvn package

# Install to local repository
mvn clean install
```

### IDE Setup

#### IntelliJ IDEA
1. Import as Maven project
2. Ensure Java 11+ SDK is configured
3. Enable annotation processing if needed

#### Eclipse
1. Import → Existing Maven Projects
2. Select the project root directory

#### VS Code
1. Install Java Extension Pack
2. Open the project folder
3. The project should be automatically recognized as a Maven project

## 📝 Contributing Guidelines

### Types of Contributions

- 🐛 **Bug fixes** - Fix existing issues
- ✨ **Features** - Add new functionality
- 📚 **Documentation** - Improve documentation
- 🧪 **Tests** - Add or improve tests
- 🎨 **Code style** - Code formatting and style improvements
- 🔧 **Infrastructure** - CI/CD, build scripts, etc.

### Development Workflow

1. **Choose an issue** or create a new one describing your contribution
2. **Create a branch** from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/issue-number-description
   ```
3. **Make your changes** following the coding standards
4. **Write tests** for new functionality
5. **Update documentation** if needed
6. **Test your changes** thoroughly
7. **Commit your changes** with clear commit messages
8. **Push to your fork** and create a pull request

## 🔄 Pull Request Process

### Before Submitting

1. **Update your branch** with the latest changes from upstream:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```
2. **Resolve any conflicts** that arise
3. **Run the full test suite**:
   ```bash
   mvn clean test
   ```
4. **Ensure code coverage** is maintained or improved

### Creating a Pull Request

1. Go to your fork on GitHub
2. Click "New Pull Request"
3. **Base repository**: `original-owner/feed4j`
4. **Base branch**: `main`
5. **Head repository**: `your-username/feed4j`
6. **Compare branch**: `your-feature-branch`
7. **Title**: Clear, descriptive title (e.g., "Add support for Atom feeds")
8. **Description**: Detailed description including:
   - What changes were made
   - Why the changes were needed
   - How to test the changes
   - Any breaking changes
   - Related issues

### Pull Request Template

Please use the following template for pull requests:

```markdown
## Description
Brief description of the changes made.

## Type of Change
- [ ] Bug fix (non-breaking change)
- [ ] New feature (non-breaking change)
- [ ] Breaking change
- [ ] Documentation update
- [ ] Code style update

## How Has This Been Tested?
Describe the tests you ran and how to reproduce the behavior.

## Checklist
- [ ] My code follows the project's style guidelines
- [ ] I have added tests for my changes
- [ ] All tests pass
- [ ] I have updated the documentation
- [ ] My changes don't break existing functionality
```

## 🎯 Coding Standards

### Java Code Style

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods small and focused (single responsibility)
- Use proper exception handling

### Code Structure

```
src/main/java/com/axeldev/
├── Feed4j.java              # Main parser class
├── Feed4jConfig.java        # Configuration class
├── FeedCache.java           # Cache implementation
├── RssFeed.java            # Feed model
└── RssItem.java            # Item model
```

### Commit Messages

Use clear, descriptive commit messages:

```
type(scope): description

[optional body]

[optional footer]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Maintenance

Examples:
```
feat(parser): add support for Atom feed format
fix(cache): resolve race condition in cache invalidation
docs(readme): update installation instructions
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=Feed4jTest

# Run with coverage
mvn test jacoco:report
```

### Writing Tests

- Use JUnit 5 for unit tests
- Place test files in `src/test/java/`
- Follow naming convention: `*Test.java`
- Aim for high code coverage (>80%)
- Test both success and failure scenarios
- Use descriptive test method names

Example test structure:
```java
public class Feed4jTest {

    @Test
    public void testParseValidRssFeed() {
        // Given
        Feed4j feed4j = new Feed4j();
        String validRssUrl = "https://example.com/rss.xml";

        // When
        RssFeed feed = feed4j.ReadFeed(validRssUrl);

        // Then
        assertNotNull(feed);
        assertNotNull(feed.getTitle());
        assertFalse(feed.getItems().isEmpty());
    }

    @Test
    public void testHandleInvalidUrl() {
        // Given
        Feed4j feed4j = new Feed4j();

        // When & Then
        assertThrows(Exception.class, () -> {
            feed4j.ReadFeed("invalid-url");
        });
    }
}
```

## 📚 Documentation

### Code Documentation

- Add JavaDoc comments for all public classes, methods, and fields
- Explain parameters, return values, and exceptions
- Provide usage examples in JavaDoc

Example:
```java
/**
 * Parses an RSS feed from the given URL.
 *
 * @param url the URL of the RSS feed to parse
 * @return the parsed RSS feed, or null if parsing failed
 * @throws IllegalArgumentException if url is null or empty
 *
 * @example
 * Feed4j feed4j = new Feed4j();
 * RssFeed feed = feed4j.ReadFeed("https://example.com/rss.xml");
 */
public RssFeed ReadFeed(String url) {
    // implementation
}
```

### README Updates

- Update README.md for new features
- Add examples for new functionality
- Keep installation and usage instructions current

## 🐛 Reporting Issues

### Bug Reports

When reporting bugs, please include:

1. **Clear title** describing the issue
2. **Steps to reproduce** the problem
3. **Expected behavior** vs actual behavior
4. **Environment details**:
   - Java version: `java -version`
   - Maven version: `mvn -version`
   - OS and version
5. **Stack traces** if applicable
6. **Sample code** that demonstrates the issue

### Feature Requests

For new features, please provide:

1. **Clear description** of the proposed feature
2. **Use case** - why is this feature needed?
3. **Proposed implementation** if you have ideas
4. **Alternatives considered**

## 📄 License

By contributing to Feed4j, you agree that your contributions will be licensed under the same license as the project (MIT License).

## 🙏 Recognition

Contributors will be acknowledged in the project documentation. Significant contributions may be recognized in release notes.

## 📞 Getting Help

If you need help or have questions:

- Check existing [issues](https://github.com/your-username/feed4j/issues) and [discussions](https://github.com/your-username/feed4j/discussions)
- Join our community discussions
- Contact the maintainers

Thank you for contributing to Feed4j! 🚀
