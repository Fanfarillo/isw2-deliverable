package deliverable.control;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONException;

import deliverable.model.Release;
import deliverable.model.ReleaseCommits;
import deliverable.model.Ticket;

public class ExecutionFlow {
	
	//This private constructor is meant to hide the public one: classes with only static methods do not have to be instantiated.
	private ExecutionFlow() {
		throw new IllegalStateException("This class does not have to be instantiated.");
	}
	
	public static void collectData(String projName) throws JSONException, IOException, ParseException, RevisionSyntaxException, GitAPIException {
		
		RetrieveJiraInfo retJiraInfo = new RetrieveJiraInfo(projName);
		List<Release> releasesList = retJiraInfo.retrieveReleases();
		List<Ticket> ticketsList = retJiraInfo.retrieveIssues(releasesList);
		
		ColdStart coldStart = new ColdStart();
		List<Ticket> otherProjConsistentTickets = coldStart.retrieveOtherConsistentIssues();
		Double p = coldStart.computeProportion(otherProjConsistentTickets);
		
		List<Ticket> consistentTicketsList = retJiraInfo.retrieveConsistentIssues(ticketsList, releasesList);
		List<Ticket> adjustedTicketsList = retJiraInfo.adjustTicketsList(ticketsList, consistentTicketsList, releasesList, p);
		
		RetrieveGitInfo retGitInfo = new RetrieveGitInfo("C:\\Users\\barba\\OneDrive\\Desktop\\Work in progress\\Progetti ISW2\\" + projName, adjustedTicketsList, releasesList);
		List<RevCommit> allCommitsList = retGitInfo.retrieveAllCommits();
		List<ReleaseCommits> relCommAssociationsList = retGitInfo.getRelCommAssociations(allCommitsList);
		List<ReleaseCommits> assocListWClasses = retGitInfo.getRelClassesAssociations(relCommAssociationsList);
		
	}

}
