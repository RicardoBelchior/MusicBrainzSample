#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Script that uses the GitHub CLI tool to create a PR, automatically filling in some of the details:
# - Ask the user if he has triggered detekt/unit tests
# - Check if release notes were added
# - Fill in the assignee and reviewers
# - Add labels based on the pr type and branch name
# - Set the base branch as `develop` or the corresponding main feature branch
# - Try to format the title based on the branch

import subprocess

REPO = "RicardoBelchior/MusicBrainzSample"
ASSIGNEE = "@me"


def run_command(command_args, capture_output=False, check=True):
    """
    Run an external command. Note, we're using check=True by default, since we always want to stop the script if a command fails.
    - capture_output: When true, captures the standard output and standard error streams of the command
    - check: When true, automatically check the return code of the subprocess, and if it is non-zero, raise a CalledProcessError.
    """
    return subprocess.run(
        command_args,
        text=True,
        check=check,
        capture_output=capture_output,
    )


def run_gh_command(
    base_branch, title, body, labels, redirect_web=True, auto_merge=False, dry_run=False
):
    if redirect_web and auto_merge:
        print("⚠️ Cannot auto-merge when redirecting to web. Ignoring auto-merge.")
        auto_merge = False

    command = ["gh", "pr", "create", "--repo", REPO, "--assignee", ASSIGNEE]
    command.extend(["--base", base_branch, "--title", title, "--body", body])

    for label in labels:
        command.extend(["--label", label])

    if redirect_web:
        command.append("--web")

    if dry_run:
        command.append("--dry-run")

    print(f"Running command: {command}")
    result = run_command(command, capture_output=True)

    # Merge the PR automatically, when approvals and checks are passed
    # Use the `-- merge` strategy to create a merge commit.
    if auto_merge and not redirect_web:
        pr_url = result.stdout.strip()
        print(pr_url)
        print("--- ")
        print(f"PR URL: {pr_url}")
        run_command(["gh", "pr", "merge", "--merge", "--auto", pr_url])


if __name__ == "__main__":
    run_gh_command(
        base_branch="main",
        title="title example",
        labels=[],
        body=f"This PR is a test.",
        redirect_web=False,
        auto_merge=True,
    )
    print("DONE.")
