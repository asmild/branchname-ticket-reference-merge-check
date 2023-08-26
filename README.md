# Branchname Jira Key Merge-check for Bitbucket

The Jira Key in Branchname Merge Check Plugin is a Bitbucket add-on that enforces the presence of a Jira key in branch names during the merge process. This plugin helps maintain consistency and traceability between Jira issues and Bitbucket branches.

## Features

- Automatically validates branch names for the presence of a Jira key.
- Prevents merges if the Jira key is missing from the branch name.
- Enhances integration and traceability between Jira and Bitbucket.

## Installation

1. Download the Branchname Jira Key Merge-check for Bitbucket from the [Bitbucket Marketplace](https://marketplace.atlassian.com/apps/<my-plugin-id>).
2. Install the plugin in your Bitbucket instance.
3. Configure the merge-check settings in repository or project settings panel.

## Usage
1. Create a new branch or update an existing branch in your Bitbucket repository.
2. Ensure that the branch name includes a valid Jira key in the format `PROJ-123`.
3. Open a pull request to merge the branch into the target branch.
4. The merge check will verify the presence of the Jira key in the branch name.
5. If the Jira key is missing, the merge process will be blocked with an error message.

## Configuration

Branchname Jira Key Merge-check for Bitbucket can be configured in the Bitbucket administration panel. Follow these steps to configure the plugin:

1. Go to the repository or project administration panel.
2. Navigate to the merge-checks and enable/edit "Jira ticket reference in branch name".
3. Customize the plugin behavior.
4. Save the configuration changes.

## License

This plugin is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for more details.

## Contributing

We welcome contributions to enhance the Branchname Jira Key Merge-check for Bitbucket. To contribute, please follow these guidelines:

1. Fork the repository and create a new branch.
2. Make your code changes or enhancements.
3. Submit a pull request with a clear description of the changes.

## Changelog

See the [CHANGELOG.md](CHANGELOG.md) file for the version history and changes made in each release.

## About

Branchname Jira Key Merge-check for Bitbucket is developed and maintained by [@asmild](https://github.com/asmild).

## Security and privacy
See the [PRIVACY](PRIVACY) file
