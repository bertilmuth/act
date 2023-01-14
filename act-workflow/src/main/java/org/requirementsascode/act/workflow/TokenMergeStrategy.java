package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;

class TokenMergeStrategy implements MergeStrategy<WorkflowState, Token>{
		private final Workflow workflow;

		public TokenMergeStrategy(Workflow workflow) {
			this.workflow = workflow;
		}
	
		@Override
		public Data<WorkflowState, Token> merge(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {
			Tokens mergedTokens = mergeTokens(datasAfter);
			WorkflowState state = new WorkflowState(workflow, mergedTokens);
			return data(state, null);
		}

		private Tokens mergeTokens(List<Data<WorkflowState, Token>> datasAfter) {	
			Map<Port<?>, List<Token>> mergedTokenMap = datasAfter.stream()
				.map(Data::state).map(WorkflowState::tokens)
				.flatMap(tkns -> tkns.asMap().entrySet().stream())
			    .collect(Collectors.toMap(
			        Map.Entry::getKey,
			        Map.Entry::getValue,
			        (v1, v2) -> { 
			        	List<Token> mergedList = Stream.concat(v1.stream(), v2.stream())
			        		.collect(Collectors.toList());
			        	return mergedList; 
			        })
			    );
			
			// Remove all elements that don't have actionData set
			mergedTokenMap.replaceAll((key, value) -> value.stream()
				.filter(t -> t.actionData().isPresent())
			    .collect(Collectors.toList()));
			
			return new Tokens(mergedTokenMap);
		}
	}