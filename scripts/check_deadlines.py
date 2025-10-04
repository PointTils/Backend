import os
import sys
import requests
from datetime import datetime, timezone

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
GITHUB_ORG = os.getenv("GITHUB_ORG")
PROJECT_NUMBER = int(os.getenv("PROJECT_NUMBER"))
DISCORD_WEBHOOK_URL = os.getenv("DISCORD_WEBHOOK_URL")

GITHUB_TO_DISCORD = {
    "caBatista": "<@188403922023612416>",
    "femelloffm": "<@688825103516958753>",
    "Iewandowski": "<@273893904988897281>",
    "MarcelloMarcon": "<@621164025195003944>",
    "GuilhermeOchoa": "<@961766744668635136>",
    "MatheusBerwaldt": "<@677320235325587475>",
    "Glauber-Developer": "<@438054668992512020>",
    "Bialves": "<@700064440451596319>",
    "AugustoPBaldino": "<@669710566587105301>",
    "viniwittler": "<@365604164019159040>",
    "timoteostifft": "<@322948532019527680>",
    "JaoVitorMS": "<@595019079945814016>",
    "CarolBrose": "<@1015742768276058203>",
    "Ferngzz": "<@129028538379534337>",
    "juliofi": "<@325383552868941826>",
    "MateusSNeubarth": "<@368177998970748939>",
    "lpinheiro05": "<@280744822778888203>",
    "joao-rangel1": "<@1278914607812968478>",
}

HEADERS = {
    "Authorization": f"Bearer {GITHUB_TOKEN}",
    "Content-Type": "application/json"
}

GRAPHQL_URL = "https://api.github.com/graphql"

def run_graphql(query, variables=None):
    response = requests.post(GRAPHQL_URL, json={"query": query, "variables": variables}, headers=HEADERS)
    response.raise_for_status()
    return response.json()

def get_project_id():
    query = """
    query ($org: String!, $number: Int!) {
      organization(login: $org) {
        projectV2(number: $number) {
          id
        }
      }
    }
    """
    result = run_graphql(query, {"org": GITHUB_ORG, "number": PROJECT_NUMBER})
    return result["data"]["organization"]["projectV2"]["id"]

def get_project_items(project_id):
    query = """
    query ($projectId: ID!, $after: String) {
      node(id: $projectId) {
        ... on ProjectV2 {
          items(first: 100, after: $after) {
            nodes {
              content {
                ... on Issue {
                  title
                  url
                  state
                  issueType {
                    name
                  }
                  labels(first: 10) {
                    nodes {
                      name
                    }
                  }
                  assignees(first: 10) {
                    nodes {
                      login
                    }
                  }
                }
              }
              fieldValues(first: 100) {
                nodes {
                  __typename
                  # Campo de data (End date)
                  ... on ProjectV2ItemFieldDateValue {
                    field {
                      ... on ProjectV2Field {
                        name
                      }
                    }
                    date
                  }
                  # Campo de iteração (Sprint / Iteration)
                  ... on ProjectV2ItemFieldIterationValue {
                    field {
                      ... on ProjectV2Field {
                        name
                      }
                    }
                    title
                    startDate
                    duration
                  }
                }
              }
            }
            pageInfo {
              hasNextPage
              endCursor
            }
          }
        }
      }
    }
    """
    all_nodes = []
    cursor = None

    while True:
        variables = {"projectId": project_id, "after": cursor}
        result = run_graphql(query, variables)
        items_block = result.get("data", {}).get("node", {}).get("items", {}) or {}
        nodes = items_block.get("nodes", []) or []
        all_nodes.extend(nodes)

        page_info = items_block.get("pageInfo", {}) or {}
        has_next = page_info.get("hasNextPage")
        end_cursor = page_info.get("endCursor")

        if not has_next:
            break
        cursor = end_cursor
        if not cursor:
            break

    # Filter out items with no content, closed issues and issues that are not sub tasks
    filtered = []
    for item in all_nodes:
        content = item.get("content")
        if not content:
            continue
        if content.get("state") == "CLOSED":
            continue
        if content.get("issueType", {}).get("name") != "Sub-Task":
            continue
        filtered.append(item)

    print(f"Fetched {len(filtered)} project items (paginated).")
    return filtered

def notify_discord(overdue_issues):
    if not overdue_issues:
        return
    content = "**⚠️ Algumas tarefas tem como prazo de entrega o dia de hoje. Saberiam dizer quando conseguirão finalizar?**\n"
    for issue in overdue_issues:
        assignees = ", ".join(issue["assignees"]) if issue["assignees"] else "_ninguém atribuído_"
        content += f"- [{issue['title']}]({issue['url']}) | Responsável(s): {assignees}\n"
    requests.post(DISCORD_WEBHOOK_URL, json={"content": content})

def validate_env():
    missing = []
    for key in ("GITHUB_TOKEN", "DISCORD_WEBHOOK_URL"):
        if not os.environ.get(key):
            missing.append(key)
    return missing

def main():
    # Check required environment variables
    missing = validate_env()
    if missing:
        print("Missing required environment variables:", ", ".join(missing))
        sys.exit(2)

    # Basic sanity check to GitHub API
    try:
        headers = {"Authorization": f"token {os.environ.get('GITHUB_TOKEN')}"}
        resp = requests.get("https://api.github.com/", headers=headers, timeout=10)
        print("GitHub API status:", resp.status_code)
    except Exception as e:
        print("Error contacting GitHub API (network/timeout):", e)
        sys.exit(3)

    print("Startup checks passed! Starting deadline check...")

    project_id = get_project_id()
    items = get_project_items(project_id)

    now = datetime.now(timezone.utc)
    overdue = []

    for item in items:
        title = item["content"]["title"]
        url = item["content"]["url"]
        assignees_raw = item["content"]["assignees"]["nodes"]
        assignees = []

        for a in assignees_raw:
            github_username = a["login"]
            discord_mention = GITHUB_TO_DISCORD.get(github_username, f"@{github_username}")
            assignees.append(discord_mention)

        for field in item["fieldValues"]["nodes"]:
            fieldData = field.get("field", {})
            if fieldData.get("name") == "End date" and field.get("date"):
                end_date = datetime.fromisoformat(field.get("date")).replace(tzinfo=timezone.utc)
                if end_date <= now:
                    overdue.append({
                        "title": title,
                        "url": url,
                        "end_date": field.get("date"),
                        "assignees": assignees
                    })

    print("Overdue: ", overdue)
    notify_discord(overdue)

if __name__ == "__main__":
    main()
